package com.romrell4.bracketchallenge.controller

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Tournament

open class UserBracketFragment: BracketFragment() {
	companion object {
		private const val BRACKET_EXTRA = "bracket"

		fun newInstance(tournament: Tournament, bracket: Bracket): UserBracketFragment {
			val fragment = UserBracketFragment()
			fragment.setArguments(tournament)
			fragment.arguments.putSerializable(BRACKET_EXTRA, bracket)
			return fragment
		}
	}

	override fun areCellsClickable() = false
	override val cellNotClickableReason: String = "You cannot edit another user's bracket"

	override fun getTextColor(playerId: Int?, predictionId: Int?, winnerId: Int?) = ContextCompat.getColor(activity,
			if (playerId == null || winnerId == null || predictionId == null) {
				//The match isn't finished, or the user hasn't selected a winner yet
				R.color.black
			} else if (predictionId == playerId) {
				//We predicted this player. If they won, green. If they lost, red.
				if (winnerId == playerId) R.color.winner_green else R.color.red
			} else {
				//We didn't predict this player. If they won, black. If they lost, gray
				if (winnerId == playerId) R.color.black else R.color.light_gray
			})

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (arguments.containsKey(BRACKET_EXTRA)) {
			bracket = arguments.getSerializable(BRACKET_EXTRA) as? Bracket
		}
	}
}