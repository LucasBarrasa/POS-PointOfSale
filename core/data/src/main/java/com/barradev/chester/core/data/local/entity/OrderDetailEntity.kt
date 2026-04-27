package com.barradev.chester.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_details",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["order_local_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["order_local_id"]),
        androidx.room.Index(value = ["product_remote_id"])
    ]
)
data class OrderDetailEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Long = 0,
    @ColumnInfo("remote_id") val remoteId: Long? = null,
    @ColumnInfo("order_local_id") val orderLocalId: Long,
    @ColumnInfo("product_remote_id") val productRemoteId: Long,
    @ColumnInfo("product_name") val productName: String,
    @ColumnInfo("product_price") val productPrice: Double,
    @ColumnInfo("quantity") val quantity: Double,
    @ColumnInfo("discount") val discount: Double,
    @ColumnInfo("subtotal") val subtotal: Double
)