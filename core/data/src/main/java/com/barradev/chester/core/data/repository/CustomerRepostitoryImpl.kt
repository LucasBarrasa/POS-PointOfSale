package com.barradev.chester.core.data.repository

import com.barradev.chester.core.data.local.dao.CustomerDao
import com.barradev.chester.core.data.mapper.toDomain
import com.barradev.chester.core.data.mapper.toEntity
import com.barradev.chester.core.data.remote.api.ChesterApiService
import com.barradev.chester.core.model.models.Customer
import com.barradev.chester.core.model.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CustomerRepositoryImpl @Inject constructor(
    private val api: ChesterApiService,
    private val dao: CustomerDao
) : CustomerRepository {

    override fun getCustomers(): Flow<List<Customer>> {
        return dao.getAllCustomers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCustomerById(id: Int): Customer {
        return dao.getCustomerById(id)!!.toDomain()
    }

    override suspend fun syncCustomers(): Result<Unit> {
        return try {
            val response = api.getCustomers()

            if (response.isSuccessful && response.body() != null) {
                val apiCustomer = response.body()

                val responseEntity = apiCustomer!!.map { it.toEntity() }
                dao.replaceAll(responseEntity)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Error del servidor"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}