package product_detail_v2.data

import product_detail_v2.data.model.ProductModel
import java.io.IOException


class ProductRepository(
    private val productLocalSource: ProductLocalSource,
    private val productServiceApi: ProductServiceApi,
) {

    suspend fun getProducts(): Result<List<ProductModel>> {
        return try {
            val localProducts = productLocalSource.getProducts()
            val result = if (localProducts.isNotEmpty()) {
                localProducts.map { it.toModel() }
            } else {
                productServiceApi.getProducts().products.map { it.toEntity() }
                    .also { entities -> productLocalSource.saveProducts(entities) }
                    .map { it.toModel() }
            }
            Result.success(result)
        } catch (e: IOException) {
            Result.failure(ProductErrors.NetworkError(e))
        } catch (e: Exception) {
            Result.failure(ProductErrors.UnknownError(e))
        }
    }
}

sealed class ProductErrors(error: Throwable? = null, message: String? = null) : Exception(error) {
    class NetworkError(error: Throwable) : ProductErrors(error)
    class LocalSourceError(error: Throwable) : ProductErrors(error)
    class UnknownError(error: Throwable) : ProductErrors(error)
}
