package com.barradev.chester.core.model.usecases.orders

import com.barradev.chester.core.model.models.QuantityAddProduct
import com.barradev.chester.core.model.repository.OrderRepository
import javax.inject.Inject


class UpdateQuantityOrderDetailUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {

    suspend operator fun invoke(
        orderId: Long,
        orderDetailId: Long,
        quantityType: QuantityAddProduct
    ): Result<Unit> {

        val totalQuantity: Result<Double> = when (quantityType) {
            is QuantityAddProduct.Delta -> {
                orderRepository.getOrderDetailQuantityById(orderDetailId)
                    .map { quantity ->
                        val newQuantity = quantity + quantityType.quantityValue

                        newQuantity.coerceAtLeast(1.0)
                    }
            }

            is QuantityAddProduct.SetSpecific -> {
                Result.success(quantityType.quantityValue)
            }
        }

        return totalQuantity.fold(
            onSuccess = { finalQuantity ->
                orderRepository.updateQuantityOrderDetail(orderId, orderDetailId, finalQuantity)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )


    }
}
