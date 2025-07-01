package com.example.fido_newsapp.ui.navigation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fido_newsapp.ui.screens.ArticleDetailScreen
import com.example.fido_newsapp.ui.screens.LifecycleAwareArticlesScreen
import com.example.fido_newsapp.ui.viewmodel.ArticlesState
import com.example.fido_newsapp.ui.viewmodel.ArticlesViewModel
import com.example.fido_newsapp.utils.Constants.ROUTE_ARTICLES
import com.example.fido_newsapp.utils.Constants.ROUTE_ARTICLE_DETAIL

fun articleDetailRoute(articleUrl: String): String = "article_detail/${Uri.encode(articleUrl)}"

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewsNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = ROUTE_ARTICLES) {
        composable(ROUTE_ARTICLES) {
            val viewModel: ArticlesViewModel = hiltViewModel()
            LifecycleAwareArticlesScreen(
                viewModel = viewModel,
                onArticleClick = { article ->
                    val encodedUrl = Uri.encode(article.url ?: "")
                    navController.navigate(articleDetailRoute(encodedUrl))
                }
            )
        }
        composable(
            route = ROUTE_ARTICLE_DETAIL,
            arguments = listOf(navArgument("articleUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(ROUTE_ARTICLES)
            }
            val viewModel: ArticlesViewModel = hiltViewModel(parentEntry)
            val state = viewModel.articlesState.collectAsState().value
            val article = when (state) {
                is ArticlesState.Success -> state.articles.find { it.url == Uri.decode(articleUrl) }
                else -> null
            }
            if (article != null) {
                ArticleDetailScreen(article = article, onBack = { navController.popBackStack() })
            } else {
                androidx.compose.material3.Text("Article not found")
            }
        }
    }
}