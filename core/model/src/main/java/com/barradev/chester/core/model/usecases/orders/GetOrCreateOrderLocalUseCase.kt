package com.barradev.chester.core.model.usecases.orders

import com.barradev.chester.core.model.models.CreateNewOrder
import com.barradev.chester.core.model.models.SyncStatus
import com.barradev.chester.core.model.repository.OrderRepository
import javax.inject.Inject

class GetOrCreateOrderLocalUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    suspend operator fun invoke(
        casualName: String?,
        casualAddress: String?,
        idCustomer: Long?,
        note: String?,
        discount: Double?
    ): Result<Long> {


        if (idCustomer != null) {
            val filteredOrderDraft = orderRepository.getOrderDraftByIdCustomer(idCustomer)

            return filteredOrderDraft.fold(onSuccess = { orderId ->
                if (orderId != null) {

                    Result.success(orderId)
                } else {
                    createNewOrder(null, null, idCustomer, note, discount)
                }

            }, onFailure = { exception ->
                Result.failure(exception)
            })

        }

        if (!casualName.isNullOrBlank()){
            return Result.failure(IllegalArgumentException("El nombre del cliente es obligatorio."))
        }

        if (!casualAddress.isNullOrBlank()){
            return Result.failure(IllegalArgumentException("La direccion del cliente es obligatoria."))
        }

        // Creacion de orden y retorno de id
        return createNewOrder(casualName, casualAddress, null, note, discount)
    }

    private suspend fun createNewOrder(
        casualName: String?,
        casualAddress: String?,
        idCustomer: Long?,
        note: String?,
        discount: Double?
    ): Result<Long> {
        val newOrder = CreateNewOrder(
            casualCustomerName = casualName,
            casualCustomerAddress = casualAddress,
            idCustomer = idCustomer,
            note = note,
            discount = discount,
            syncStatus = SyncStatus.DRAFT
        )
        return orderRepository.createNewOrder(newOrder)
    }

}