package com.romrell4.bracketchallenge.controller

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.romrell4.bracketchallenge.R

class AddTournamentDialog(context: Context): Dialog(context) {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dialog_add_tournament)

		//TODO: Make this work
	}
}