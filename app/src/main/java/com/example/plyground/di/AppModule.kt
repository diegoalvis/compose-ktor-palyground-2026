package com.example.plyground.di

import android.content.Context
import androidx.room.Room
import com.example.plyground.data.local.ProductDao
import com.example.plyground.data.local.ProductDatabase
import com.example.plyground.data.remote.ProductApiService
import com.example.plyground.data.repository.ProductRepositoryImpl
import com.example.plyground.domain.repository.ProductRepository
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

    val productRepository: ProductRepository by lazy {
        val productDao: ProductDao = database.productDao()
        val apiService = ProductApiService(httpClient)
        ProductRepositoryImpl(productDao, apiService)
    }

}
