package com.romrell4.bracketchallenge.model

import com.facebook.AccessToken
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Created by romrell4 on 2/25/18
 */
class Client {
    companion object {
        fun createApi(): Api {
            return Retrofit.Builder()
                    .baseUrl("https://3vxcifd2rc.execute-api.us-west-2.amazonaws.com/PROD/")
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
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
        @GET("tournaments")
        fun getTournaments(): Call<List<Tournament>>
    }
}