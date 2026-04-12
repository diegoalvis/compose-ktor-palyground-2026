package com.example.plyground.data.remote

import android.util.Log
import com.example.plyground.data.remote.dto.ProductDto
import com.example.plyground.data.remote.dto.ProductResponseDto
import com.example.plyground.data.util.retryWithBackoff
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProductApiService(private val client: HttpClient) {
    suspend fun fetchProducts(): List<ProductDto> {
        val url = "https://dummyjson.com/products?limit=10&skip=10&select=id,title,price,description,thumbnail"
        return retryWithBackoff {
            client.get(url).body<ProductResponseDto>().products.also {
                Log.d("ProductApiService", "Fetched ${it.size} products")
            }
        }
    }
}
