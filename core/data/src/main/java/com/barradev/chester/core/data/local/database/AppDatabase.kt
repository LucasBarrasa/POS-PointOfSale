package com.barradev.chester.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.barradev.chester.core.data.local.converters.SyncStatusConverter
import com.barradev.chester.core.data.local.dao.CustomerDao
import com.barradev.chester.core.data.local.dao.OrderDao
import com.barradev.chester.core.data.local.dao.ProductDao
import com.barradev.chester.core.data.local.entity.CustomerEntity
import com.barradev.chester.core.data.local.entity.OrderDetailEntity
import com.barradev.chester.core.data.local.entity.OrderEntity
import com.barradev.chester.core.data.local.entity.ProductEntity

@Database(
    entities = [
        CustomerEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(SyncStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
}