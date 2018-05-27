package com.romrell4.bracketchallenge.controller

import android.os.Bundle
import android.view.View
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Tournament
import com.romrell4.bracketchallenge.support.Identity
import retrofit2.Response

/**
 * Created by romrell4 on 3/25/18
 */
class ResultsBracketFragment: BracketFragment() {
	companion object {
		fun newInstance(tournament: Tournament): ResultsBracketFragment {
			val fragment = ResultsBracketFragment()
			fragment.setArguments(tournament)
			return fragment
		}
	}

	//Abstract properties
	override fun areCellsClickable() = Identity.user.admin
	override val cellNotClickableReason: String = "Only admins can update the results bracket"

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		loadBracket()
	}

	private fun loadBracket() {
		tournament.masterBracketId?.let {
			Client.createApi().getBracket(tournament.tournamentId, it).enqueue(object: Client.SimpleCallback<Bracket>(activity) {
				override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
					bracket = data
				}
			})
		}
	}
}