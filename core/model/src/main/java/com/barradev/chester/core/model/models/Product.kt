package com.barradev.chester.core.model.models

data class Product(
    val id: Long,
    val idRemote: Long,
    val name: String,
    val price: Double,
    val currentStock: Double,
    val imageUrl: String,
    val category: Int
)
