package com.romrell4.bracketchallenge.support

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.romrell4.bracketchallenge.model.User

/**
 * Created by romrell4 on 3/24/18
 */
object Identity {
	lateinit var user: User
		private set

	private const val SHARED_PREF_NAME = "app"
	private const val USER_PREF_KEY = "user"
	private val gson = Gson()

	fun load(context: Context): User? {
		return getPrefs(context).getString(USER_PREF_KEY, null)?.let { userJson ->
			gson.fromJson<User>(userJson, User::class.java).also { user = it }
		}
	}

	fun saveUser(context: Context, user: User) {
		this.user = user

		getPrefs(context).edit().putString(USER_PREF_KEY, gson.toJson(user)).apply()
	}

	private fun getPrefs(context: Context) = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
}