package com.example.fido_newsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fido_newsapp.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.first
import kotlin.collections.isNotEmpty

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    private val repository: ArticlesRepository
) : ViewModel() {

    private val _sourcesState = MutableStateFlow<SourcesState>(SourcesState.Loading)
    val sourcesState: StateFlow<SourcesState> = _sourcesState.asStateFlow()

    private val _articlesState = MutableStateFlow<ArticlesState>(ArticlesState.Initial)
    val articlesState: StateFlow<ArticlesState> = _articlesState.asStateFlow()

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadSources()
    }

    fun loadSources() {
        viewModelScope.launch {
            _sourcesState.value = SourcesState.Loading
            try {
                repository.refreshSources()
                val sources = repository.getSources()
                _sourcesState.value = SourcesState.Success(sources)
                // On first load, fetching articles for the first source
                if (sources.isNotEmpty()) {
                    loadArticles(sources.first().id)
                }
            } catch (e: Exception) {
                _sourcesState.value = SourcesState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    fun refreshSources() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.refreshSources()
                val sources = repository.getSources()
                _sourcesState.value = SourcesState.Success(sources)
                val currentSourceId = sources.getOrNull(selectedTabIndex.value)?.id
                if (currentSourceId != null) {
                    loadArticles(currentSourceId)
                }
            } catch (e: Exception) {
                _sourcesState.value = SourcesState.Error(e.message ?: "Failed to refresh sources")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadArticles(sourceId: String) {
        viewModelScope.launch {
            // Showing cached immediately to avoid delay
            val cachedArticles = repository.getArticles(sourceId)
            _articlesState.value = ArticlesState.Success(cachedArticles)

            //  fetching latest in background
            try {
                repository.refreshArticles(sourceId)
                val freshArticles = repository.getArticles(sourceId)
                _articlesState.value = ArticlesState.Success(freshArticles)
            } catch (e: Exception) {
                 _articlesState.value = ArticlesState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun refreshArticles() {
        val sources = (sourcesState.value as? SourcesState.Success)?.sources
        val currentSourceId = sources?.getOrNull(selectedTabIndex.value)?.id
        if (currentSourceId != null) {
            viewModelScope.launch {
                _isRefreshing.value = true
                try {
                    repository.refreshArticles(currentSourceId)
                    val freshArticles = repository.getArticles(currentSourceId)
                    _articlesState.value = ArticlesState.Success(freshArticles)
                } catch (e: Exception) {
                    _articlesState.value = ArticlesState.Error(e.message ?: "Failed to refresh articles")
                } finally {
                    _isRefreshing.value = false
                }
            }
        } else {
            loadSources()
        }
    }



    fun setSelectedTabIndex(index: Int) {
        _selectedTabIndex.value = index
        val sources = (sourcesState.value as? SourcesState.Success)?.sources.orEmpty()
        sources.getOrNull(index)?.let { source ->
            loadArticles(source.id)
        }
    }
}