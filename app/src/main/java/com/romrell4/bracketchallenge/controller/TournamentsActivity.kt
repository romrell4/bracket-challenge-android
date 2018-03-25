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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tournaments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMATTER = SimpleDateFormat("MMM d", Locale.US)
private const val LOGIN_VIEW_INDEX = 0
private const val TOURNAMENTS_VIEW_INDEX = 1

class TournamentsActivity: AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    private var adapter = TournamentAdapter(emptyList())

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
        menuInflater.inflate(R.menu.menu_tournaments, menu)
        //TODO: Make the menu change when logged out
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.logout -> {
            LoginManager.getInstance().logOut()
            checkLoginStatus()
            true
        }
        R.id.addTournament -> {
            //TODO: Add logic for add a tournament
            false
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() == null) {
            println(AccessToken.getCurrentAccessToken())
            viewSwitcher.displayedChild = LOGIN_VIEW_INDEX
            loginButton.setReadPermissions("email")
            callbackManager = CallbackManager.Factory.create()
            loginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    checkLoginStatus()
                }

                override fun onCancel() {
                    Toast.makeText(this@TournamentsActivity, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@TournamentsActivity, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            viewSwitcher.displayedChild = TOURNAMENTS_VIEW_INDEX
            loadData()
        }

        //This will reload what the options menu has in it
        invalidateOptionsMenu()
    }

    private fun loadData() {
        swipeRefreshLayout.isRefreshing = true

        //Make the HTTP call to load tournaments (using the access token as a header)
        Client.createApi().getTournaments().enqueue(object: Callback<List<Tournament>> {
            override fun onResponse(call: Call<List<Tournament>>?, response: Response<List<Tournament>>?) {
                adapter.tournaments = response?.body().orEmpty()
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Tournament>>?, t: Throwable?) {
                Toast.makeText(this@TournamentsActivity, "An error occurred. Details: ${t?.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    inner class TournamentAdapter(var tournaments: List<Tournament>): RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {

        override fun getItemCount() = tournaments.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TournamentViewHolder(layoutInflater.inflate(R.layout.row_tournament, parent, false))
        override fun onBindViewHolder(holder: TournamentViewHolder, position: Int) {
            holder.bind(tournaments[position])
        }

        inner class TournamentViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
            val datesTextView = view.findViewById<TextView>(R.id.datesTextView)

            fun bind(tournament: Tournament) {
                if (!tournament.imageUrl.isNullOrEmpty()) {
                    Picasso.with(this@TournamentsActivity)
                            .load(tournament.imageUrl)
                            .placeholder(android.R.color.darker_gray)
                            .into(imageView)
                }
                nameTextView.text = tournament.name
                datesTextView.text = resources.getString(R.string.dates_format, DATE_FORMATTER.format(tournament.startDate), DATE_FORMATTER.format(tournament.endDate))
            }
        }
    }
}
