package products.plyground.di

import android.content.Context
import androidx.room.Room
import products.plyground.data.local.ProductDao
import products.plyground.data.local.ProductDatabase
import products.plyground.data.remote.ProductApiService
import products.plyground.data.repository.ProductRepositoryImpl
import products.plyground.domain.repository.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppModule {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val database: ProductDatabase by lazy {
        Room.databaseBuilder(appContext, ProductDatabase::class.java, "products.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    private val httpClient: HttpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    val productRepository: ProductRepository
        get() {
            val productDao: ProductDao = database.productDao()
            val apiService = ProductApiService(httpClient)
            return ProductRepositoryImpl(productDao, apiService)
        }

}
