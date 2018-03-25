package com.romrell4.bracketchallenge.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Tournament
import kotlinx.android.synthetic.main.activity_tournament_tab.*

class TournamentTabActivity: AppCompatActivity() {
    companion object {
        const val TOURNAMENT_EXTRA = "tournament"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tournament_tab)

        fragmentManager.beginTransaction().replace(R.id.frameLayout, MyBracketFragment.newInstance()).commit()

        val tournament = intent.getParcelableExtra<Tournament>(TOURNAMENT_EXTRA)
        println(tournament)

        navView.setOnNavigationItemSelectedListener {
            fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, when (it.itemId) {
                        R.id.my_bracket -> MyBracketFragment.newInstance()
                        R.id.results -> ResultsBracketFragment.newInstance()
                        R.id.standings -> StandingsFragment.newInstance()
                        else -> null
                    })
                    .commit()
            true
        }
    }
}
