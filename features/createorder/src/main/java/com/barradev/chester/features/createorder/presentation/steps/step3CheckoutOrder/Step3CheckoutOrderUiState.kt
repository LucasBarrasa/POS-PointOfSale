package com.barradev.chester.features.createorder.presentation.steps.step3CheckoutOrder


sealed interface Step3CheckoutOrderUiState{
    object Loading : Step3CheckoutOrderUiState
    data class Error(val message: String): Step3CheckoutOrderUiState
    data class Success(
        val clientName: String,
        val clientAddress: String,
        val totalItems: Double,
        val totalAmount: String,
        val itemsList: List<CheckoutItemUi>
    ): Step3CheckoutOrderUiState
}

data class CheckoutItemUi(
    val idOrderDetail: Long,
    val nameProduct: String,
    val quantity: String,
    val unitPrice: String,
    val subTotalPrice: String
)


sealed interface Step3UiEvent {
    data class ShowSnackbar(val message: String) : Step3UiEvent
}
