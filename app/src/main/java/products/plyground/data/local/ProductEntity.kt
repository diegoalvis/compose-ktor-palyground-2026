package products.plyground.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import products.plyground.domain.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val isFavorite: Boolean = false
)

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    isFavorite = isFavorite
)
