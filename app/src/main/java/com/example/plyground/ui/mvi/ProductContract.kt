package com.example.plyground.ui.mvi

import com.example.plyground.domain.model.Product

data class ProductState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedProductId: String? = null,
    val sortOrder: SortOrder = SortOrder.DEFAULT
) {
    val filteredProducts: List<Product>
        get() {
            val filtered = if (searchQuery.isBlank()) products
                           else products.filter {
                               it.name.contains(searchQuery, ignoreCase = true)
                           }
            return when (sortOrder) {
                SortOrder.DEFAULT    -> filtered
                SortOrder.PRICE_ASC  -> filtered.sortedBy { it.price }
                SortOrder.PRICE_DESC -> filtered.sortedByDescending { it.price }
                SortOrder.NAME       -> filtered.sortedBy { it.name }
            }
        }
}

enum class SortOrder { DEFAULT, PRICE_ASC, PRICE_DESC, NAME }

sealed interface ProductIntent {
    data object LoadProducts : ProductIntent
    data class Search(val query: String) : ProductIntent
    data class SelectProduct(val id: String) : ProductIntent
    data class ToggleFavorite(val id: String) : ProductIntent
    data class ChangeSortOrder(val order: SortOrder) : ProductIntent
}

sealed interface ProductEffect {
    data class NavigateToDetail(val productId: String) : ProductEffect
    data class ShowError(val message: String) : ProductEffect
}
