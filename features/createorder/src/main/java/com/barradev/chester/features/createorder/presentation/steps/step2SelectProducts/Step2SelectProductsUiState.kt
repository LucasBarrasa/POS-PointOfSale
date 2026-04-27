package com.barradev.chester.features.createorder.presentation.steps.step2SelectProducts

import androidx.compose.runtime.Immutable


@Immutable
data class Step2SelectProductsUiState(
    val productsList: List<ProductItemUiState > = emptyList(),
    val searchQuery: String = "",
    val totalItemsCount: Double = 0.0,
    val totalAmount: Double = 0.0
)

@Immutable
data class ProductItemUiState(
    val id: Long,
    val orderDetailId: Long?,
    val name: String,
    val price: Double,
    val currentStock: Double,
    val quantityInOrder: Double,
    val isAdded: Boolean = quantityInOrder > 0
)