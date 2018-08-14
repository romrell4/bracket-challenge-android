package com.romrell4.bracketchallenge.controller

import android.os.Bundle
import android.view.View
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Match
import com.romrell4.bracketchallenge.model.Tournament
import com.romrell4.bracketchallenge.support.Identity
import com.romrell4.bracketchallenge.support.showLoadingDialog
import com.romrell4.bracketchallenge.support.visible
import kotlinx.android.synthetic.main.fragment_bracket.*
import retrofit2.Response
import java.util.*

/**
 * Created by romrell4 on 3/25/18
 */
class MyBracketFragment: UserBracketFragment() {
	companion object {
		private const val CREATE_BRACKET_INDEX = 1

		fun newInstance(tournament: Tournament): MyBracketFragment {
			val fragment = MyBracketFragment()
			fragment.setArguments(tournament)
			return fragment
		}
	}

	//Abstract properties
	override fun areCellsClickable() = tournament.startDate?.after(Date()) ?: false
	override val cellNotClickableReason: String = "This tournament has already begun. You cannot make any more edits to your bracket"

	//Lifecycle

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		loadBracket()
	}

	//Custom functions

	private fun loadBracket() {
		bracket = Bracket(null, null, null, "Test", 0, listOf(
				listOf(
						createMatch(1, 2),
						createMatch(3, 4),
						createMatch(5, 6),
						createMatch(7, 8),
						createMatch(9, 10),
						createMatch(11, 12),
						createMatch(13, 14),
						createMatch(15, 16),
						createMatch(17, 18),
						createMatch(19, 20),
						createMatch(21, 22),
						createMatch(23, 24),
						createMatch(25, 26),
						createMatch(27, 28),
						createMatch(29, 30),
						createMatch(31, 32)
				),
				listOf(
						createMatch(1, 3),
						createMatch(5, 7),
						createMatch(9, 11),
						createMatch(13, 15),
						createMatch(17, 19),
						createMatch(21, 23),
						createMatch(25, 27),
						createMatch(29, 31)
				),
				listOf(
						createMatch(1, 5),
						createMatch(9, 13),
						createMatch(17, 21),
						createMatch(25, 29)
				)
		))
//		Client.createApi().getMyBracket(tournament.tournamentId).enqueue(object: Callback<Bracket> {
//			override fun onResponse(call: Call<Bracket>?, response: Response<Bracket>?) {
//				//A 404 means that we don't have a bracket yet. If not, allow the user to create one
//				if (response?.code() == 404) {
//					setupCreateBracketUI()
//				} else if (response?.body() != null) {
//					bracket = response.body()
//				}
//			}
//
//			override fun onFailure(call: Call<Bracket>?, t: Throwable?) {
//				activity.showToast(activity.getString(R.string.error_message, t?.message), Toast.LENGTH_LONG)
//			}
//		})
	}

	private var matchId = 1
	private fun createMatch(player1Id: Int, player2Id: Int): Match {
		val (round, position) = when {
			matchId <= 16 -> Pair(1, matchId)
			matchId <= 24 -> Pair(2, matchId - 16)
			matchId <= 28 -> Pair(3, matchId - 24)
			matchId <= 30 -> Pair(4, matchId - 28)
			else -> Pair(5, matchId - 30)
		}
		return Match(matchId++, 0, round, position, player1Id, "Test $player1Id", player2Id, "Test $player2Id", null, null, player1Id, "Test $player1Id")
	}

	private fun setupCreateBracketUI() {
		viewSwitcher.displayedChild = CREATE_BRACKET_INDEX

		if (areCellsClickable()) {
			createBracketButton.setOnClickListener {
				val dialog = activity.showLoadingDialog()
				Client.createApi().createBracket(tournament.tournamentId, Bracket(name = Identity.user.name)).enqueue(object: Client.SimpleCallback<Bracket>(activity) {
					override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
						dialog.dismiss()

						data?.let {
							bracket = it
						}
					}
				})
			}
		} else {
			createBracketButton.visible = false
			createBracketTextView.text = getString(R.string.tournament_started_cannot_create_bracket)
		}
	}
}