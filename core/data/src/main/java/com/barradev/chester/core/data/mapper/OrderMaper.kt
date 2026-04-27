package com.barradev.chester.core.data.mapper

import com.barradev.chester.core.data.local.entity.OrderDetailEntity
import com.barradev.chester.core.data.local.entity.OrderEntity
import com.barradev.chester.core.data.local.relation.OrderSummaryTuple
import com.barradev.chester.core.data.local.relation.OrderAggregate
import com.barradev.chester.core.data.remote.dto.OrderDetailDto
import com.barradev.chester.core.data.remote.dto.OrdersDto
import com.barradev.chester.core.model.models.OrderCompleteAggregate
import com.barradev.chester.core.model.models.OrderSummary
import com.barradev.chester.core.model.models.OrderDetail
import com.barradev.chester.core.model.models.SyncStatus


// Mapeo de Cabecera (Order)
fun OrdersDto.toEntity(status: SyncStatus): OrderEntity {
    return OrderEntity(
        localId = 0,
        remoteId = this.id, // ID del Servidor
        customerId = this.customerId,
        customerName = this.customerName,
        createdDate = this.createdDate,
        amount = this.amount,
        address = this.address,
        note = this.note,
        pdf = this.pdf,
        discount = this.discount,
        syncStatus = status
    )
}


fun OrderSummaryTuple.toDomain(): OrderSummary {

    val finalAmount = if (this.order.syncStatus == SyncStatus.DRAFT) {
        this.computedTotal
    } else {
        this.order.amount ?: this.computedTotal
    }

    return OrderSummary(
        localId = this.order.localId,
        remoteOrderId = this.order.remoteId,
        fullNameCustomer = this.resolvedCustomerName,
        addressCustomer = this.resolvedCustomerAddress,
        createdDate = this.order.createdDate,
        note = this.order.note ?: "",
        pdf = this.order.pdf ?: "",
        discount = this.order.discount ?: 0.0,
        totalAmount = finalAmount,
        totalItems = this.itemCount,
        status = this.order.syncStatus
    )

}

fun OrderAggregate.toDomain(): OrderCompleteAggregate {

    val finalName = this.customer?.let {
        "${it.firstName} ${it.lastName}".trim()
    } ?: this.order.customerName
    ?: "Cliente desconocido"

    val finalAddress = this.customer?.address
        ?: this.order.address
        ?: "Sin direccion"


    val orderHeader = OrderSummary(
        localId = this.order.localId,
        remoteOrderId = this.order.remoteId ?: 0,
        fullNameCustomer = finalName,
        addressCustomer = finalAddress,
        createdDate = this.order.createdDate,
        note = this.order.note ?: "",
        pdf = this.order.pdf ?: "",
        discount = this.order.discount ?: 0.0,
        totalAmount = this.order.amount ?: 0.0,
        totalItems = this.details.sumOf { it.quantity },
        status = this.order.syncStatus
    )

    return OrderCompleteAggregate(
        orderSummary = orderHeader,
        detailsList = this.details.map { it.toDomain() }
    )
}


fun OrderDetailDto.toEntity(orderIdLocal: Long): OrderDetailEntity {

    return OrderDetailEntity(
        id = 0,
        remoteId = this.orderId,
        orderLocalId = orderIdLocal,
        productRemoteId = this.product.idRemote,
        productName = this.product.name,
        productPrice = this.product.price,
        quantity = this.quantity,
        discount = this.discount,
        subtotal = (this.productPrice ?: this.product.price) * (this.quantity)
    )
}





fun OrderDetailEntity.toDomain(): OrderDetail {
    return OrderDetail(
        id = this.id,
        idLocalOrder = this.orderLocalId,
        productRemoteId = this.productRemoteId,
        productName = this.productName,
        productPrice = this.productPrice,
        quantity = this.quantity,
        discount = this.discount,
        subTotal = this.subtotal,
    )
}