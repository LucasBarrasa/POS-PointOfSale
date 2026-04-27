package com.barradev.chester.core.data.local.entity


import android.text.BoringLayout
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo("remote_id")
    val remoteId: Long,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @ColumnInfo(name = "address")
    val address: String?,

    @ColumnInfo(name = "phone")
    val phone: String?,

    @ColumnInfo(name = "has_current_account")
    val hasCurrentAccount: Boolean

)