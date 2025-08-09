package com.example.fakestoreecommerceapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val images: List<String> = emptyList(),
    val category: Category? = null
)