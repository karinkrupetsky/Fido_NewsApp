package com.example.fido_newsapp.repository

import com.example.fido_newsapp.model.Article
import com.example.fido_newsapp.model.NewsSource

interface ArticlesRepository {

    suspend fun getSources(
        category: String? = null,
        language: String? = null,
        country: String? = null
    ): List<NewsSource>

    suspend fun refreshSources(
        category: String? = null,
        language: String? = null,
        country: String? = null
    )

    suspend fun getArticles(sourceId: String): List<Article>
    suspend fun refreshArticles(sourceId: String)

}