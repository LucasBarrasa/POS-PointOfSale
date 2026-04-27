package com.barradev.chester.core.model.models

data class OrderDetail(
    val id: Long = 0,
    val idLocalOrder: Long,
    val productRemoteId: Long,
    val productName: String,
    val productPrice: Double,
    val quantity: Double,
    val discount: Double,
    val subTotal: Double
)
