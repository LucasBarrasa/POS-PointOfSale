package com.barradev.chester.core.model.models

import java.time.LocalDateTime

data class OrderSummary(
    val localId: Long,
    val remoteOrderId: Long?,
    val fullNameCustomer: String,
    val addressCustomer: String,
    val createdDate: String,
    val note: String,
    val pdf: String,
    val discount: Double,
    val totalAmount: Double,
    val totalItems: Double,
    val status: SyncStatus
)