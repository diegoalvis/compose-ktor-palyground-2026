package product_detail_v2.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import product_detail_v2.data.model.ProductModel


interface ProductLocalSource {

    suspend fun getProducts(): List<ProductEntity>

    suspend fun saveProducts(products: List<ProductEntity>)

}

class ProductLocalSourceImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ProductLocalSource {

    private val products = mutableListOf<ProductEntity>()

    override suspend fun getProducts(): List<ProductEntity> {
        return withContext(dispatcher) {
            products
        }
    }

    override suspend fun saveProducts(products: List<ProductEntity>) {
        withContext(dispatcher) {
            this@ProductLocalSourceImpl.products.addAll(products)
        }
    }

}


@Serializable
data class ProductEntity(
    val id: Long,
    val title: String,
    val price: Double,
    val description: String,
    val thumbnail: String,
)



fun ProductEntity.toModel(): ProductModel {
    return ProductModel(
        id = id,
        title = title,
        price = price,
        description = description,
        thumbnail = thumbnail,
    )
}