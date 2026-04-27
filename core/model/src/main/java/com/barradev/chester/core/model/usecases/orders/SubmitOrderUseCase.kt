package com.barradev.chester.core.model.usecases.orders

import com.barradev.chester.core.model.models.OrderDetailRequest
import com.barradev.chester.core.model.models.ProductRequest
import com.barradev.chester.core.model.models.RequestCreationOrder
import com.barradev.chester.core.model.models.SyncStatus
import com.barradev.chester.core.model.repository.OrderRepository
import javax.inject.Inject


class SubmitOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(orderId: Long): Result<Unit> {

        val orderComplete = orderRepository.getOrderAggregate(orderId).getOrElse { error ->
            return Result.failure(error)
        }

        if (orderComplete.detailsList.isEmpty()) {
            return Result.failure(Exception("No puedes enviar una orden vacia"))
        }

        val statusUpdateResult = orderRepository.updateSyncStatus(orderId, SyncStatus.SYNCING)

        if (statusUpdateResult.isFailure) {
            return Result.failure(statusUpdateResult.exceptionOrNull() ?: Exception("Error al actualizar el estado de la orden"))
        }

        return orderRepository.uploadOrder(orderId)
    }
}