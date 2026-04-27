package com.barradev.chester.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OrderDetailDto(
    @SerialName("id") val id: Long,
    @SerialName("order") val orderId: Long,
    @SerialName("product") val product: ProductDto,
    @SerialName("state") val state: Boolean,
    @SerialName("created_date") val createdDate: String,
    @SerialName("modified_date") val modifiedDate: String,
    @SerialName("product_price") val productPrice: Double?,
    @SerialName("quantity") val quantity: Double,
    @SerialName("discount") val discount: Double,
)
