package com.romrell4.bracketchallenge.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Created by romrell4 on 2/25/18
 */
class Client {
    interface Api {
        @GET("tournaments")
        fun getTournaments(): Call<List<Tournament>>
    }
}