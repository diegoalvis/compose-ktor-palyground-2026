package products.plyground.domain.repository

import products.plyground.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(limit: Int = 20, offset: Int = 0): Flow<List<Product>>

    suspend fun fetchProducts(): Result<List<Product>>
    suspend fun toggleFavorite(productId: String)
    suspend fun getProductById(id: String): Result<Product>
}
