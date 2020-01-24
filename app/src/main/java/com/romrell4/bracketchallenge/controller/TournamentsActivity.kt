package com.romrell4.bracketchallenge.controller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.romrell4.bracketchallenge.BuildConfig
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Tournament
import com.romrell4.bracketchallenge.model.User
import com.romrell4.bracketchallenge.support.Identity
import com.romrell4.bracketchallenge.support.showLoadingDialog
import com.romrell4.bracketchallenge.support.showToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tournaments.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMATTER = SimpleDateFormat("MMM d", Locale.US)

class TournamentsActivity: AppCompatActivity() {
	//TODO: Difference between qualifiers and bye

	private val adapter = TournamentAdapter()
	private val api = Client.createApi()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_tournaments)

		FirebaseApp.initializeApp(this)

		FirebaseAuth.getInstance().addAuthStateListener {
			if (it.currentUser != null) {
				val alert = showLoadingDialog()
				api.login().enqueue(object: Client.SimpleCallback<User>(this@TournamentsActivity) {
					override fun onResponse(data: User?, errorResponse: Response<User>?) {
						alert.dismiss()
						data?.let { user ->
							Identity.saveUser(this@TournamentsActivity, user)
							checkLoginStatus()
						}
					}
				})
			} else {
				checkLoginStatus()
			}
		}

		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter

		//Allow the user to pull down on the recycler view to refresh the tournaments
		swipeRefreshLayout.setOnRefreshListener {
			loadData()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		if (FirebaseAuth.getInstance().currentUser != null) {
			menuInflater.inflate(R.menu.menu_tournaments, menu)
		}
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
		R.id.logout -> {
			AuthUI.getInstance().signOut(this)
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	private fun checkLoginStatus() {
		if (FirebaseAuth.getInstance().currentUser == null) {
			//Clear the adapter
			adapter.tournaments = emptyList()

			startActivity(AuthUI.getInstance().createSignInIntentBuilder()
					.setLogo(R.drawable.app_icon)
					.setIsSmartLockEnabled(!BuildConfig.DEBUG)
					.setAvailableProviders(listOf(
							AuthUI.IdpConfig.GoogleBuilder(),
							AuthUI.IdpConfig.FacebookBuilder(),
							AuthUI.IdpConfig.EmailBuilder()
					).map { it.build() })
					.build())
		} else if (adapter.tournaments.isEmpty()) { //Only reload the data if we haven't loaded already
			supportActionBar?.subtitle = "Logged in as ${Identity.user.name}"
			loadData()
		}

		//This will reload what the options menu has in it
		invalidateOptionsMenu()
	}

	private fun loadData() {
		swipeRefreshLayout.isRefreshing = true

		//Make the HTTP call to load tournaments (using the access token as a header)
		api.getTournaments().enqueue(object: Client.SimpleCallback<List<Tournament>>(this) {
			override fun onResponse(data: List<Tournament>?, errorResponse: Response<List<Tournament>>?) {
				swipeRefreshLayout.isRefreshing = false
				data?.let {
					adapter.tournaments = it
					adapter.notifyDataSetChanged()
				}
			}
		})
	}

	inner class TournamentAdapter(var tournaments: List<Tournament> = emptyList()): RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {

		override fun getItemCount() = tournaments.size
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TournamentViewHolder(layoutInflater.inflate(R.layout.row_tournament, parent, false))
		override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
			holder.bind(tournaments[position])
		}

		inner class TournamentViewHolder(view: View): RecyclerView.ViewHolder(view) {
			private val imageView = view.findViewById<ImageView>(R.id.imageView)
			private val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
			private val datesTextView = view.findViewById<TextView>(R.id.datesTextView)

			fun bind(tournament: Tournament) {
				if (!tournament.imageUrl.isNullOrEmpty()) {
					Picasso.get()
							.load(tournament.imageUrl)
							.placeholder(android.R.color.darker_gray)
							.into(imageView)
				}
				nameTextView.text = tournament.name
				datesTextView.text = resources.getString(R.string.dates_format, DATE_FORMATTER.format(tournament.startDate), DATE_FORMATTER.format(tournament.endDate))

				itemView.setOnClickListener {
					if (tournament.masterBracketId != null) {
						startActivity(Intent(this@TournamentsActivity, TournamentTabActivity::class.java)
								.putExtra(TournamentTabActivity.TOURNAMENT_EXTRA, tournament))
					} else {
						showToast(R.string.no_draws, Toast.LENGTH_LONG)
					}
				}
			}
		}
	}
}
