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
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Tournament
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_tournaments.*
import java.text.SimpleDateFormat
import java.util.*

private val DATE_FORMATTER = SimpleDateFormat("M/d/yy", Locale.US)

class TournamentsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournaments)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TournamentAdapter(listOf(
                Tournament(0, "Test", null, null, "https://www.atpworldtour.com/en/scores/current/acapulco/807/draws         | https://rafaelnadalfans.files.wordpress.com/2017/03/rafa-nadal-beats-mischa-zverev-in-straight-sets-in-acapulco-2017-mexican-open-2.jpg?w=326&h=217", Calendar.getInstance().time, Calendar.getInstance().time)
        ))
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

    inner class TournamentAdapter(val tournaments: List<Tournament>): RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder>() {
        override fun getItemCount() = tournaments.size
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = TournamentViewHolder(layoutInflater.inflate(R.layout.row_tournament, parent, false))
        override fun onBindViewHolder(holder: TournamentViewHolder?, position: Int) {
            holder?.bind(tournaments[position])
        }

        inner class TournamentViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
            val datesTextView = view.findViewById<TextView>(R.id.datesTextView)

            fun bind(tournament: Tournament) {
//                Picasso.with(this@TournamentsActivity).load(tournament.imageUrl).into(imageView)
                nameTextView.text = tournament.name
                datesTextView.text = resources.getString(R.string.dates_format, DATE_FORMATTER.format(tournament.startDate), DATE_FORMATTER.format(tournament.endDate))
            }
        }
    }
}
