package com.barradev.chester.core.model.models

data class CreateNewOrder(
    val casualCustomerName: String?,
    val casualCustomerAddress: String?,
    val idCustomer: Long?,
    val note: String?,
    val discount: Double?,
    val syncStatus: SyncStatus
)
