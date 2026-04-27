package com.barradev.chester.core.model.models



data class RequestCreationOrder(
    val detail: List<OrderDetailRequest>
)

data class OrderDetailRequest(
    val remoteProduct: ProductRequest,
    val quantity: Double
)

data class ProductRequest(
    val idRemote: Long,
)
