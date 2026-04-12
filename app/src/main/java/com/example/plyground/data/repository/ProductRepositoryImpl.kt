package com.example.plyground.data.repository

import android.util.Log
import com.example.plyground.data.local.ProductDao
import com.example.plyground.data.local.ProductEntity
import com.example.plyground.data.local.toDomain
import com.example.plyground.data.remote.dto.toEntity
import com.example.plyground.data.remote.ProductApiService
import com.example.plyground.domain.model.Product
import com.example.plyground.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class ProductRepositoryImpl(
    private val dao: ProductDao,
    private val api: ProductApiService,
    private val dispatcherIo: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {

    override fun observeProducts(limit: Int, offset: Int): Flow<List<Product>> =
        dao.observeAll(limit, offset).map { entities -> entities.map { it.toDomain() } }
            .onEach { products ->
                Log.d("Repository", "Emitted ${products.size} products")
            }

    override suspend fun fetchProducts(): Result<List<Product>> {
        return runCatching {
            withContext(dispatcherIo) {
                val remoteProducts = api.fetchProducts()
                val entities = remoteProducts.map { it.toEntity() }
                dao.upsertAll(entities)
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun toggleFavorite(productId: String) {
        dao.toggleFavorite(productId)
    }

    override suspend fun getProductById(id: String): Product? =
        dao.getById(id)?.toDomain()
}
