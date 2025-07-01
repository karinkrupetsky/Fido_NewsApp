package com.example.fido_newsapp.ui.viewmodel

import com.example.fido_newsapp.model.NewsSource

sealed class SourcesState {
    object Loading : SourcesState()
    data class Success(val sources: List<NewsSource>) : SourcesState()
    data class Error(val message: String) : SourcesState()
}