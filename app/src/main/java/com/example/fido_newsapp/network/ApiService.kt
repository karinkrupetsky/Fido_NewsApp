package com.example.fido_newsapp.network

import com.example.fido_newsapp.model.NewsApiResponse
import com.example.fido_newsapp.model.SourcesApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("sources") source: String,
        @Query("apiKey") apiKey: String
    ): NewsApiResponse

    @GET("sources")
    suspend fun getSources(
        @Query("apiKey") apiKey: String,
        @Query("category") category: String? = null,
        @Query("language") language: String? = null,
        @Query("country") country: String? = null
    ): SourcesApiResponse
}