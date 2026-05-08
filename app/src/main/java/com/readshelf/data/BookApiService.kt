package com.readshelf.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.readshelf.BuildConfig

interface BookApiService {
    @GET("volumes")
    fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String = BuildConfig.BOOKS_API_KEY
    ): Call<BookResponse>
}