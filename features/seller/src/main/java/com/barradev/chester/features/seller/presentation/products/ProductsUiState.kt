package com.barradev.chester.features.seller.presentation.products

import com.barradev.chester.core.model.models.Product


data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isSyncing: Boolean = false
)
