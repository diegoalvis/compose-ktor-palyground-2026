package com.example.plyground.ui.navigation

sealed class Screen(val route: String) {
    data object ProductList   : Screen("product_list")
    data object ProductListMvi   : Screen("product_list_mvi")
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(id: String) = "product_detail/$id"
    }
}
