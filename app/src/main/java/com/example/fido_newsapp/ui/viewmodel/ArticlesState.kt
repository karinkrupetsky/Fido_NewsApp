package com.example.fido_newsapp.ui.viewmodel

import com.example.fido_newsapp.model.Article

sealed class ArticlesState {
    object Initial : ArticlesState()
    object Loading : ArticlesState()
    data class Success(val articles: List<Article>) : ArticlesState()
    data class Error(val message: String) : ArticlesState()
}