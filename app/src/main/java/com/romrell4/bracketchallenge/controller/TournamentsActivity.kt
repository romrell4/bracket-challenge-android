package com.romrell4.bracketchallenge.controller

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
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Tournament
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tournaments.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMATTER = SimpleDateFormat("MMM d", Locale.US)

class TournamentsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournaments)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TournamentAdapter(emptyList())
        recyclerView.adapter = adapter

        val api = Retrofit.Builder()
                .baseUrl("https://3vxcifd2rc.execute-api.us-west-2.amazonaws.com/PROD/")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .setDateFormat("yyyy-MM-dd")
                        .create()))
                .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .build()
                .create(Client.Api::class.java)
        api.getTournaments().enqueue(object: Callback<List<Tournament>> {
            override fun onResponse(call: Call<List<Tournament>>?, response: Response<List<Tournament>>?) {
                adapter.tournaments = response?.body().orEmpty()
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<Tournament>>?, t: Throwable?) {
                Toast.makeText(this@TournamentsActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tournaments, menu)
//        menu?.findItem(R.id.addTournament)?.isVisible =
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.logout -> {
            true
        }
        R.id.addTournament -> {
            false
        }
        else -> super.onOptionsItemSelected(item)
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
