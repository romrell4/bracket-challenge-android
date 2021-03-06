package com.romrell4.bracketchallenge.model

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.romrell4.bracketchallenge.R
import com.romrell4.bracketchallenge.support.showToast
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException

/**
 * Created by romrell4 on 2/25/18
 */
class Client {
	companion object {
		fun createApi(): Api {
			return Retrofit.Builder()
					.baseUrl("https://3vxcifd2rc.execute-api.us-west-2.amazonaws.com/PROD/")
					.addConverterFactory(GsonConverterFactory.create(GsonBuilder()
							.registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
							.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
							.setDateFormat("yyyy-MM-dd")
							.create()))
					.client(OkHttpClient.Builder()
							.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
							.addInterceptor { chain ->
								val requestBuilder = chain.request().newBuilder()

								//If the user is logged in, attach a token to the request
								FirebaseAuth.getInstance().currentUser?.let { user ->
									Tasks.await(user.getIdToken(true)).token?.let {
										//This line will allow you to view and copy the token, for debug purposes
										println(it)
										chain.proceed(requestBuilder.addHeader("X-Firebase-Token", it).build())
									} ?: throw Exception("Unable to retrieve token")
								} ?: run {
									//If the user isn't logged in, let them call the public endpoints
									chain.proceed(requestBuilder.build())
								}
							}
							.build())
					.build()
					.create(Client.Api::class.java)
		}
	}

	interface Api {
		@POST("users")
		fun login(): Call<User>

		@GET("tournaments")
		fun getTournaments(): Call<List<Tournament>>

		@GET("tournaments/{tournamentId}/brackets")
		fun getBrackets(@Path("tournamentId") tournamentId: Int): Call<List<Bracket>>

		@POST("tournaments/{tournamentId}/brackets")
		fun createBracket(@Path("tournamentId") tournamentId: Int, @Body bracket: Bracket): Call<Bracket>

		@GET("tournaments/{tournamentId}/brackets/mine")
		fun getMyBracket(@Path("tournamentId") tournamentId: Int): Call<Bracket>

		@GET("tournaments/{tournamentId}/brackets/{bracketId}")
		fun getBracket(@Path("tournamentId") tournamentId: Int, @Path("bracketId") bracketId: Int): Call<Bracket>

		@PUT("tournaments/{tournamentId}/brackets/{bracketId}")
		fun updateBracket(@Path("tournamentId") tournamentId: Int, @Path("bracketId") bracketId: Int, @Body bracket: Bracket): Call<Bracket>
	}

	abstract class SimpleCallback<T>(private val context: Context): Callback<T> {
		abstract fun onResponse(data: T?, errorResponse: Response<T>? = null)

		override fun onResponse(call: Call<T>?, response: Response<T>?) {
			val data = response?.body()
			if (response?.isSuccessful == true && data != null) {
				onResponse(data)
			} else {
				onFailure(call, Throwable(response?.errorBody()?.string()))
				onResponse(null, errorResponse = response)
			}
		}

		override fun onFailure(call: Call<T>?, t: Throwable?) {
			context.showToast(context.getString(R.string.error_message, t?.message), Toast.LENGTH_LONG)
		}
	}

	class BooleanTypeAdapter: TypeAdapter<Boolean>() {
		override fun write(out: JsonWriter?, value: Boolean?) {

		}

		override fun read(`in`: JsonReader?) = when (`in`?.peek()) {
			JsonToken.NUMBER -> `in`.nextInt() != 0
			JsonToken.BOOLEAN -> `in`.nextBoolean()
			else -> throw IOException("Expected number or boolean, but got ${`in`?.peek()}")
		}
	}
}
