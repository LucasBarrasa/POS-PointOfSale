package com.barradev.chester.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.barradev.chester.core.model.models.SyncStatus
import kotlinx.serialization.SerialName

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val localId: Long = 0,
    @ColumnInfo("remote_id") val remoteId: Long? = null, // Id Servidor
    @ColumnInfo("created_date") val createdDate: String,
    @ColumnInfo("customer_name") val customerName: String?,
    @ColumnInfo("address") val address: String?,
    @ColumnInfo("amount") val amount: Double?,
    @ColumnInfo("note") val note: String?,
    @ColumnInfo("pdf") val pdf: String?,
    @ColumnInfo("customer_id") val customerId: Long?,
    @ColumnInfo("discount") val discount: Double?,

    @ColumnInfo("sync_status") val syncStatus: SyncStatus // Para evaluar sincronizacion con servidor
)