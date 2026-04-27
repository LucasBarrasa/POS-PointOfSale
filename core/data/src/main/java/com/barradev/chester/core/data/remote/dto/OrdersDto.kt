package com.barradev.chester.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class OrdersDto(
    @SerialName("id") val id: Long,
    @SerialName("created_date") val createdDate: String,
    @SerialName("customer_name") val customerName: String?,
    @SerialName("amount") val amount: Double,
    @SerialName("address") val address: String?,
    @SerialName("note") val note: String?,
    @SerialName("pdf") val pdf: String?,
    @SerialName("customer") val customerId: Long?,
    @SerialName("discount") val discount: Double?,
)