package com.barradev.chester.core.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmCreateOrderResponseDto(
    @SerialName("message") val message: String,
    @SerialName("order") val order: OrderCreatedDto
)

@Serializable
data class OrderCreatedDto(
    @SerialName("id") val id: Long
    // No mapeamos el resto porque dijiste que solo te importa el ID y el mensaje
)