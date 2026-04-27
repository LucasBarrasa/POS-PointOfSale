package com.barradev.chester.features.seller.presentation.customers

import com.barradev.chester.core.model.models.Customer


data class CustomerSellerUiState(
    val customers: List<Customer> = emptyList(),
    val searchQuery: String = "",
    val isSyncing: Boolean = false
)