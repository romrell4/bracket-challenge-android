package com.romrell4.bracketchallenge.support

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.romrell4.bracketchallenge.model.User

/**
 * Created by romrell4 on 3/24/18
 */
class Identity {
    companion object {
        private const val USER_PREF_KEY = "user"
        lateinit var user: User private set
        val gson = Gson()

        fun load(context: Context): User? {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            if (prefs.contains(USER_PREF_KEY)) {
                try {
                    user = gson.fromJson<User>(prefs.getString(USER_PREF_KEY, null), User::class.java)
                    return user
                } catch (e: JsonSyntaxException) {
                    println("Error loading user json: $e")
                }
            }
            return null
        }

        fun saveUser(context: Context, user: User) {
            this.user = user

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(USER_PREF_KEY, gson.toJson(user)).apply()
        }
    }
}