package com.romrell4.bracketchallenge.controller

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.model.Bracket
import com.romrell4.bracketchallenge.model.Client
import com.romrell4.bracketchallenge.model.Tournament
import com.romrell4.bracketchallenge.support.Identity
import com.romrell4.bracketchallenge.support.showLoadingDialog
import com.romrell4.bracketchallenge.support.showToast
import kotlinx.android.synthetic.main.fragment_bracket.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Created by romrell4 on 3/25/18
 */
class MyBracketFragment: BracketFragment() {
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

	//Lifecycle

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		loadBracket()
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
		R.id.saveBracket -> {
			bracket?.let { it ->
				it.tournamentId?.let { tournamentId ->
					it.bracketId?.let { bracketId ->
						Client.createApi().updateBracket(tournamentId, bracketId, it).enqueue(object: Client.SimpleCallback<Bracket>(activity) {
							override fun onResponse(data: Bracket?, errorResponse: Response<Bracket>?) {
								data?.let { newBracket ->
									activity.showToast(R.string.bracket_update_success)
									bracket = newBracket
								}
							}
						})
					}
				}
			}
			true
		}
		else -> false
	}

	//Custom functions

	private fun loadBracket() {
		Client.createApi().getMyBracket(tournament.tournamentId).enqueue(object: Callback<Bracket> {
			override fun onResponse(call: Call<Bracket>?, response: Response<Bracket>?) {
				//A 404 means that we don't have a bracket yet. If not, allow the user to create one
				if (response?.code() == 404) {
					setupCreateBracketUI()
				} else if (response?.body() != null) {
					bracket = response.body()
				}
			}

			override fun onFailure(call: Call<Bracket>?, t: Throwable?) {
				activity.showToast(activity.getString(R.string.error_message, t?.message), Toast.LENGTH_LONG)
			}
		})
	}

	private fun setupCreateBracketUI() {
		viewSwitcher.displayedChild = CREATE_BRACKET_INDEX

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
	}
}