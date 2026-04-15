package products.plyground.data.local

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.IGNORE
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC LIMIT :limit OFFSET :offset")
    fun observeAll(limit: Int = 20, offset: Int = 0): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): List<ProductEntity>

    @Upsert
    suspend fun upsertAll(products: List<ProductEntity>)

    @Insert(onConflict = IGNORE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("UPDATE products SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProductEntity?
}
