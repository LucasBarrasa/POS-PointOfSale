package com.barradev.chester.core.model.repository

import com.barradev.chester.core.model.models.Product
import kotlinx.coroutines.flow.Flow


interface ProductRepository{
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(idProduct: Long): Product
    suspend fun syncProducts(): Result<Unit>
}