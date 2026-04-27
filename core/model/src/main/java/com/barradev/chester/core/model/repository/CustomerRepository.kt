package com.barradev.chester.core.model.repository


import com.barradev.chester.core.model.models.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    // Observa la BD (Reactivo)
    fun getCustomers(): Flow<List<Customer>>

    // Obtiene un cliente específico
    suspend fun getCustomerById(id: Int): Customer?

    // Llama a la API y actualiza la BD
    suspend fun syncCustomers(): Result<Unit>
}