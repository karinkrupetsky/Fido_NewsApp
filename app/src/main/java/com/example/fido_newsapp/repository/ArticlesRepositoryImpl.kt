package com.example.fido_newsapp.repository

import com.example.fido_newsapp.model.Article
import com.example.fido_newsapp.model.NewsSource
import com.example.fido_newsapp.network.ApiService
import com.example.fido_newsapp.utils.Constants.NEWS_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticlesRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ArticlesRepository {

    private val articlesCache = mutableMapOf<String, List<Article>>()
    private val sourcesCache = mutableMapOf<Triple<String?, String?, String?>, List<NewsSource>>()

    override suspend fun getSources(
        category: String?, language: String?, country: String?
    ): List<NewsSource> = withContext(Dispatchers.IO) {
        sourcesCache[Triple(category, language, country)] ?: emptyList()
    }

    override suspend fun refreshSources(
        category: String?, language: String?, country: String?
    ) = withContext(Dispatchers.IO) {
        val response = apiService.getSources(
            apiKey = NEWS_API_KEY, category = category, language = language, country = country
        )
        sourcesCache[Triple(category, language, country)] = response.sources
    }

    override suspend fun getArticles(sourceId: String): List<Article> =
        withContext(Dispatchers.IO) {
            articlesCache[sourceId] ?: emptyList()
        }

    override suspend fun refreshArticles(sourceId: String) = withContext(Dispatchers.IO) {
        val response = apiService.getTopHeadlines(
            source = sourceId, apiKey = NEWS_API_KEY
        )
        articlesCache[sourceId] = response.articles
    }

}