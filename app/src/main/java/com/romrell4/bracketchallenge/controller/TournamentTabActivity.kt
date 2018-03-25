package com.romrell4.bracketchallenge.controller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Tournament

class TournamentTabActivity: AppCompatActivity() {
    companion object {
        const val TOURNAMENT_EXTRA = "tournament"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_tab)

        val tournament = intent.getParcelableExtra<Tournament>(TOURNAMENT_EXTRA)
        println(tournament)
    }
}
