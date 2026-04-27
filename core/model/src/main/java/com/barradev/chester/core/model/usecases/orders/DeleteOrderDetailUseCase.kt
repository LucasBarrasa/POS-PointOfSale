package com.barradev.chester.core.model.usecases.orders

import com.barradev.chester.core.model.repository.OrderRepository
import javax.inject.Inject

class DeleteOrderDetailUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke (orderId: Long, orderDetail: Long) {
        orderRepository.deleteOrderDetail(orderId,orderDetail)
    }
}