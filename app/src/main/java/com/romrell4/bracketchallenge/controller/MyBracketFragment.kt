package com.romrell4.bracketchallenge.controller

import android.os.Bundle
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
import com.romrell4.bracketchallenge.support.visible
import kotlinx.android.synthetic.main.fragment_bracket.*
import retrofit2.Call
import retrofit2.Callback
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