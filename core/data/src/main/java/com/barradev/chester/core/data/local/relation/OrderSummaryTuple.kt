package com.barradev.chester.core.data.local.relation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.barradev.chester.core.data.local.entity.OrderEntity


data class OrderSummaryTuple(
    @Embedded val order: OrderEntity,

    @ColumnInfo("resolved_customer_name") val resolvedCustomerName: String,
    @ColumnInfo("resolved_customer_address") val resolvedCustomerAddress: String,

    @ColumnInfo("items_count") val itemCount: Double,
    @ColumnInfo("computed_total") val computedTotal: Double
)
