package com.example.plyground.data.remote.dto

import com.example.plyground.data.local.ProductEntity
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val thumbnail: String? = null,
)

fun ProductDto.toEntity() = ProductEntity(
    id = id,
    name = title,
    description = description,
    price = price,
    imageUrl = thumbnail.orEmpty(),
)


@Serializable
data class ProductResponseDto(
    val products: List<ProductDto>,
)