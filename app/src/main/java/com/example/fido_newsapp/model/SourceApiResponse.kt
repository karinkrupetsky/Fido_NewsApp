package com.example.fido_newsapp.model

data class SourcesApiResponse(
    val status: String?,
    val sources: List<NewsSource>
)