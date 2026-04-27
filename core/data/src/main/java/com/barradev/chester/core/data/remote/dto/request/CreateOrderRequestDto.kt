package com.barradev.chester.core.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class CreateOrderRequestDto(
    @SerialName("id") val idRemoteOrder: Int?,
    @SerialName("detail") val detail: List<CreateOrderDetailRequestDto>
)

@Serializable
data class CreateOrderDetailRequestDto(
    @SerialName("product") val productIdRemote: ProductIdRequestDto,
    @SerialName("quantity") val quantity: Double
)

@Serializable
data class ProductIdRequestDto(
    @SerialName("id") val id: Long,
)