package product_detail_v2.data

import kotlinx.serialization.Serializable
import product_detail_v2.data.model.ProductModel

interface ProductServiceApi {

    suspend fun getProducts(): ProductListResponse

}

class ProductServiceApiImp : ProductServiceApi {
    override suspend fun getProducts(): ProductListResponse {
        val mocklist = listOf(
            ProductDto(
                id = 11,
                title = "Annibale Colombo Bed",
                price = 1899.99,
                description = "The Annibale Colombo Bed is a luxurious and elegant bed frame, crafted with high-quality materials for a comfortable and stylish bedroom.",
                thumbnail = "https://cdn.dummyjson.com/product-images/furniture/annibale-colombo-bed/thumbnail.webp"
            )
        )
        return ProductListResponse(mocklist)
    }
}


/*
{
  "
  ": [
    {
      "id": 11,
      "title": "Annibale Colombo Bed",
      "price": 1899.99,
      "description": "The Annibale Colombo Bed is a luxurious and elegant bed frame, crafted with high-quality materials for a comfortable and stylish bedroom.",
      "thumbnail": "https://cdn.dummyjson.com/product-images/furniture/annibale-colombo-bed/thumbnail.webp"
    },
    { ... }
    ]
 }
 */

@Serializable
data class ProductListResponse(
    val products: List<ProductDto>
)

@Serializable
data class ProductDto(
    val id: Long,
    val title: String,
    val price: Double,
    val description: String,
    val thumbnail: String,
)


fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        thumbnail = thumbnail
    )
}
