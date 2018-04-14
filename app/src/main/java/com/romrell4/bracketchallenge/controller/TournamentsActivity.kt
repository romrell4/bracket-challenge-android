package com.romrell4.bracketchallenge.controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
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

class TournamentsActivity: AppCompatActivity() {
	companion object {
		private val DATE_FORMATTER = SimpleDateFormat("MMM d", Locale.US)
		private const val LOGIN_VIEW_INDEX = 0
		private const val TOURNAMENTS_VIEW_INDEX = 1
	}

	private val callbackManager = CallbackManager.Factory.create()
	private val adapter = TournamentAdapter()
	private val loggedIn get() = AccessToken.getCurrentAccessToken() != null && Identity.load(this) != null
	private val api = Client.createApi()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_tournaments)

		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter

		//Allow the user to pull down on the recycler view to refresh the tournaments
		swipeRefreshLayout.setOnRefreshListener {
			loadData()
		}
	}

	override fun onStart() {
		super.onStart()

		checkLoginStatus()
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		if (loggedIn) {
			menuInflater.inflate(R.menu.menu_tournaments, menu)
//			menu?.findItem(R.id.addTournament)?.isVisible = Identity.user.admin
		}
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
		R.id.logout -> {
			LoginManager.getInstance().logOut()
			checkLoginStatus()
			true
		}
//		R.id.addTournament -> {
//			AddTournamentDialog(this).show()
//			true
//		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		callbackManager?.onActivityResult(requestCode, resultCode, data)
	}

	private fun checkLoginStatus() {
		if (!loggedIn) {
			//Clear the adapter
			adapter.tournaments = emptyList()

			viewSwitcher.displayedChild = LOGIN_VIEW_INDEX

			supportActionBar?.subtitle = "Not logged in"

			loginButton.setReadPermissions("email")
			loginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
				override fun onSuccess(result: LoginResult?) {
					println("Access token: ${AccessToken.getCurrentAccessToken()}")

					val alert = showLoadingDialog()
					api.login().enqueue(object: Client.SimpleCallback<User>(this@TournamentsActivity) {
						override fun onResponse(data: User?, errorResponse: Response<User>?) {
							alert.dismiss()
							data?.let {
								Identity.saveUser(this@TournamentsActivity, it)
								checkLoginStatus()
							}
						}
					})
				}

				override fun onCancel() = showToast(R.string.login_failed_message)
				override fun onError(error: FacebookException?) = showToast(R.string.login_failed_message)
			})
		} else if (adapter.tournaments.isEmpty()) { //Only reload the data if we haven't loaded already
			viewSwitcher.displayedChild = TOURNAMENTS_VIEW_INDEX
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
					Picasso.with(this@TournamentsActivity)
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
