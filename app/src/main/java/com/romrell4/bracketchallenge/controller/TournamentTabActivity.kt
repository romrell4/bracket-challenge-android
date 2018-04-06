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


		val tournament = intent.getParcelableExtra<Tournament>(TOURNAMENT_EXTRA)
		fragmentManager.beginTransaction().replace(R.id.frameLayout, MyBracketFragment.newInstance(tournament)).commit()
		println(tournament)

		//TODO: Figure out a way to save the fragments (show/hide?) https://github.com/roughike/BottomBar/issues/385
		navView.setOnNavigationItemSelectedListener {
			fragmentManager.beginTransaction()
					.replace(R.id.frameLayout, when (it.itemId) {
						R.id.my_bracket -> MyBracketFragment.newInstance(tournament)
						R.id.results -> ResultsBracketFragment.newInstance(tournament)
						R.id.standings -> StandingsFragment.newInstance(tournament)
						else -> null
					})
					.commit()
			true
		}
	}
}
