package com.barradev.chester.core.model.usecases.orders

import com.barradev.chester.core.model.models.OrderDetail
import com.barradev.chester.core.model.repository.OrderRepository
import com.barradev.chester.core.model.repository.ProductRepository
import javax.inject.Inject

class CreateOrderDetailUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        orderId: Long,
        productId: Long
    ) {

        val productToAdd = productRepository.getProductById(productId)

        val orderDetail = OrderDetail(
            idLocalOrder = orderId,
            productRemoteId = productToAdd.idRemote,
            productName = productToAdd.name,
            productPrice = productToAdd.price,
            quantity = 1.0,
            discount = 0.0,
            subTotal = productToAdd.price
        )

        orderRepository.createOrderDetail(orderDetail = orderDetail)
    }
}