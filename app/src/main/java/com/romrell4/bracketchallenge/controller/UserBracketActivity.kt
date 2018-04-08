package com.romrell4.bracketchallenge.controller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Tournament

class UserBracketActivity: AppCompatActivity() {
	companion object {
		const val BRACKET_EXTRA = "bracket"
		const val TOURNAMENT_EXTRA = "tournament"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_bracket)

		val tournament = intent.getParcelableExtra<Tournament>(TOURNAMENT_EXTRA)
		val bracket = intent.getSerializableExtra(BRACKET_EXTRA) as Bracket

		title = bracket.name

		fragmentManager.beginTransaction()
				.replace(R.id.frameLayout, UserBracketFragment.newInstance(tournament, bracket))
				.commit()
	}
}
