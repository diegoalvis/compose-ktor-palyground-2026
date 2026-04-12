package com.example.plyground.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.plyground.di.AppModule
import com.example.plyground.ui.detail.ProductDetailScreen
import com.example.plyground.ui.mvi.ProductMviScreen
import com.example.plyground.ui.mvi.ProductMviViewModel
import com.example.plyground.ui.mvvm.ProductListScreen
import com.example.plyground.ui.mvvm.ProductListViewModel

@Composable
fun ProductNavGraph(
    navController: NavHostController,
//    startDestination: String = Screen.ProductList.route,
    startDestination: String = Screen.ProductListMvi.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        val viewModel by lazy { ProductListViewModel(AppModule.productRepository) }
        val viewModelMvi by lazy { ProductMviViewModel(AppModule.productRepository) }
        composable(Screen.ProductList.route) {
            ProductListScreen(
                viewModel = viewModel,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(Screen.ProductListMvi.route) {
            ProductMviScreen(
                viewModel = viewModelMvi,
                onNavigateToDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
