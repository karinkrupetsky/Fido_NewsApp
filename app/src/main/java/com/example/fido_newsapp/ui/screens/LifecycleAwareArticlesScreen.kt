package com.example.fido_newsapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.fido_newsapp.model.Article
import com.example.fido_newsapp.ui.viewmodel.ArticlesViewModel
import com.example.fido_newsapp.ui.viewmodel.SourcesState

@Composable
fun LifecycleAwareArticlesScreen(
    viewModel: ArticlesViewModel,
    onArticleClick: (Article) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // This ensures that when the user returns to this screen (via back navigation, or app comes to foreground),
    // the app will refresh both sources and articles.
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSources()
                viewModel.refreshArticles()
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val articlesState = viewModel.articlesState.collectAsState().value
    val isRefreshing = viewModel.isRefreshing.collectAsState().value
    val sourcesState = viewModel.sourcesState.collectAsState().value
    val selectedTab = viewModel.selectedTabIndex.collectAsState().value
    val sources = when (sourcesState) {
        is SourcesState.Success -> sourcesState.sources
        else -> emptyList()
    }

    ArticlesScreen(
        state = articlesState,
        isRefreshing = isRefreshing,
        sources = sources,
        selectedTab = selectedTab,
        onTabSelected = { viewModel.setSelectedTabIndex(it) },
        onRefresh = { viewModel.refreshArticles() },
        onArticleClick = onArticleClick
    )
}