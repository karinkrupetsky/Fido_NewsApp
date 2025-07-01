package com.example.fido_newsapp.model

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)