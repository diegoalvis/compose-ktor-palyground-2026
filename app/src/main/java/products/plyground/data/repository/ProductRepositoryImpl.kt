package products.plyground.data.repository

import android.util.Log
import products.plyground.data.local.ProductDao
import products.plyground.data.local.ProductEntity
import products.plyground.data.local.toDomain
import products.plyground.data.remote.dto.toEntity
import products.plyground.data.remote.ProductApiService
import products.plyground.domain.model.Product
import products.plyground.domain.repository.ProductRepository
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
                dao.insertAll(entities)
                entities.map { it.toDomain() }
            }
        }
    }

    override suspend fun toggleFavorite(productId: String) {
        dao.toggleFavorite(productId)
    }

    override suspend fun getProductById(id: String): Result<Product> =
        runCatching {
            dao.getById(id)?.toDomain()!!
        }
}
