package com.barradev.chester.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id") val idRemote: Long,
    val name: String,
    val price: Double,
    val stock: Double,
    @SerialName("stock_control") val stockControl: Boolean,
    @SerialName("image") val imageUrl: String? = null,
    @SerialName("category") val categoryId: Int? = null,
    @SerialName("modified_date")val modifiedDate: String,
    @SerialName("deleted_date") val deletedDate: String?

)
