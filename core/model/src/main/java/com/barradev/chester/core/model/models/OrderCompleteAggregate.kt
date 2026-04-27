package com.barradev.chester.core.model.models


data class OrderCompleteAggregate(
    val orderSummary: OrderSummary,
    val detailsList : List<OrderDetail>
)
