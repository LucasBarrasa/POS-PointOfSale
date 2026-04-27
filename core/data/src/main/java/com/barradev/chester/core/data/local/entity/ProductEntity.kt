package com.barradev.chester.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName


@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo("id_remote") val idRemote: Long,
    val name: String,
    val price: Double,
    val stock: Double,

    @ColumnInfo("has_stock_control") val hasStockControl: Boolean,

    @ColumnInfo("image_url") val imageUrl: String?,

    @ColumnInfo("category_id") val categoryId: Int,

    @ColumnInfo("modificated_date") val modificatedDate: String,

    @ColumnInfo("delete_date") val deletedDate: String?
)
