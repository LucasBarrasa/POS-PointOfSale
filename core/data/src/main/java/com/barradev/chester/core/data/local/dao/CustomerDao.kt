package com.barradev.chester.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.barradev.chester.core.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY first_name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    // Para buscar un cliente específico (ej: al crear orden)
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerEntity?

    // Estrategia REPLACE: Si ya existe el ID, lo actualiza con los datos nuevos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    @Query("DELETE FROM customers")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(listCustomer: List<CustomerEntity>){
        clearAll()
        insertAll(listCustomer)
    }
}