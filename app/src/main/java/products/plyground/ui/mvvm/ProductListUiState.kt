package products.plyground.ui.mvvm

import products.plyground.domain.model.Product

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedProductIds: List<String> = emptyList()
) {
    val filteredProducts: List<Product>
        get() = if (searchQuery.isBlank()) products
                else products.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }

    val selectedProducts: List<Product>
        get() = products.filter { it.id in selectedProductIds }
}
