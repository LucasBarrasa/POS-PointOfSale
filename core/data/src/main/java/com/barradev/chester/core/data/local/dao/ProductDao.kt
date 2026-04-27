package com.barradev.chester.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.barradev.chester.core.data.local.entity.ProductEntity
import com.barradev.chester.core.model.models.Product
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id IN (:ids)")
    suspend fun getProductsByListIds(ids: List<Long>): List<ProductEntity>

    @Query("Select * FROM products WHERE id = :idProduct")
    suspend fun getProductById(idProduct: Long): ProductEntity


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAll()


    @Transaction
    suspend fun replaceAll(products: List<ProductEntity>) {
        clearAll()
        insertAll(products)
    }


    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Int, newStock: Int)


}