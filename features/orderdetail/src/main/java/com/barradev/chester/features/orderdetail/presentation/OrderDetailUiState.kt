package com.barradev.chester.features.orderdetail.presentation

import com.barradev.chester.core.model.models.OrderCompleteAggregate

sealed interface OrderDetailUiState {
    data object Loading : OrderDetailUiState
    data class Error(val errorMessage: String) : OrderDetailUiState
    data class Success(
        val topBarTitle: String,
        val headerName: String,
        val headerAddress: String,
        val headerDate: String,
        val headerItemsCount: String,
        val headerTotalAmount: String,
        val products: List<ProductDetailUi>
    ) : OrderDetailUiState
}

data class ProductDetailUi(
    val id: Long,
    val quantity: String,
    val name: String,
    val unitPrice: String,
    val subTotal: String
)