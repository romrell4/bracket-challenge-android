package com.romrell4.bracketchallenge.model

import android.content.Context
import android.widget.Toast
import com.facebook.AccessToken
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.romrell4.bracketchallenge.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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
                                chain.proceed(chain.request().newBuilder().addHeader("Token", AccessToken.getCurrentAccessToken().token).build())
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
            Toast.makeText(context, context.getString(R.string.error_message, t?.message), Toast.LENGTH_LONG).show()
        }
    }

    class BooleanTypeAdapter: TypeAdapter<Boolean>() {
        override fun write(out: JsonWriter?, value: Boolean?) {

        }

        override fun read(`in`: JsonReader?) = when(`in`?.peek()) {
            JsonToken.NUMBER -> `in`.nextInt() != 0
            JsonToken.BOOLEAN -> `in`.nextBoolean()
            else -> throw IOException("Expected number or boolean, but got ${`in`?.peek()}")
        }
    }
}
