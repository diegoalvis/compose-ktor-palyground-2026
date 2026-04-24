package product_detail_v2.data.model

import kotlinx.serialization.Serializable


@Serializable
data class ProductModel(
    val id: Long,
    val title: String,
    val price: Double,
    val description: String,
    val thumbnail: String,
)