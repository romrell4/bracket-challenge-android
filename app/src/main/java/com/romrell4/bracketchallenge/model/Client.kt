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
        @Headers("Token: EAAExIiMPbR4BAGa3YARwubrOGaa6ma3mbvCZBniDA1HEpObOKPqJF2nBC6mBGqttpxuw9YxflrmZATEHjKqHRZCoahRzYVjWJBiHOUitgKb0ZBax21IzlyffybeI7bW5V4yoGSOuKWiBDNtMd8pNUF9DJ2wk1h1VZBKDdChtUZCZCyp9N3gt3hf8MjDkLvZBZApZAIOMFOZAPhYKSkTi4eelV3H0UzGFPRZCdtjsK6q0WIEUAVqlVSNbo6xZC")
        fun getTournaments(): Call<List<Tournament>>
    }
}