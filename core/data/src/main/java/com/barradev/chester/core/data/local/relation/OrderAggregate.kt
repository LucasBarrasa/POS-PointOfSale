package com.barradev.chester.core.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.barradev.chester.core.data.local.entity.CustomerEntity
import com.barradev.chester.core.data.local.entity.OrderDetailEntity
import com.barradev.chester.core.data.local.entity.OrderEntity


data class OrderAggregate(
    @Embedded val order: OrderEntity,

    @Relation(
        parentColumn = "customer_id",
        entityColumn = "remote_id"
    )
    val customer: CustomerEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "order_local_id"
    )
    val details: List<OrderDetailEntity>
)
