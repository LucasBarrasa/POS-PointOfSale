package com.barradev.chester.core.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponseDto(
    @SerialName("error") val errorMessages: List<String> ,
)