package com.barradev.chester.core.data.mapper

import com.barradev.chester.core.data.remote.dto.request.CreateOrderDetailRequestDto
import com.barradev.chester.core.data.remote.dto.request.CreateOrderRequestDto
import com.barradev.chester.core.data.remote.dto.request.ProductIdRequestDto
import com.barradev.chester.core.model.models.OrderDetailRequest
import com.barradev.chester.core.model.models.RequestCreationOrder

fun RequestCreationOrder.toDto(): CreateOrderRequestDto {
    return CreateOrderRequestDto(
        idRemoteOrder = null,
        detail = this.detail.map { it.toDto() }
    )
}


fun OrderDetailRequest.toDto(): CreateOrderDetailRequestDto {
    return CreateOrderDetailRequestDto(
        productIdRemote = ProductIdRequestDto(id = this.remoteProduct.idRemote),
        quantity = this.quantity
    )
}
