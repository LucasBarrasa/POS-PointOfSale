package com.barradev.chester.core.data.repository

import com.barradev.chester.core.data.di.IoDispatchers
import com.barradev.chester.core.data.local.dao.ProductDao
import com.barradev.chester.core.data.mapper.toDomain
import com.barradev.chester.core.data.mapper.toEntity
import com.barradev.chester.core.data.remote.api.ChesterApiService
import com.barradev.chester.core.model.models.Product
import com.barradev.chester.core.model.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor (
    private val api: ChesterApiService,
    private val dao: ProductDao,
    @param:IoDispatchers private val ioDispatchers: CoroutineDispatcher
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return dao.getAllProducts().map { productsEntities ->
            productsEntities.map { it.toDomain() }
        }
    }

    override suspend fun getProductById(idProduct: Long): Product {
        return withContext(ioDispatchers){
            return@withContext dao.getProductById(idProduct).toDomain()
        }
    }

    override suspend fun syncProducts(): Result<Unit> {
        return try {

            val response = api.getProducts()

            if (response.isSuccessful && response.body() != null){
                val apiProducts = response.body()!!

                val responseEntity = apiProducts.map { it.toEntity() }
                dao.replaceAll(responseEntity)

                Result.success(Unit)
            }else{
                Result.failure(Exception("Error del servidor"))
            }


        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}