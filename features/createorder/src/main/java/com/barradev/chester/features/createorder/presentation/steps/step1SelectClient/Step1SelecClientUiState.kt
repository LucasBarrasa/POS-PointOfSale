package com.barradev.chester.features.createorder.presentation.steps.step1SelectClient

import androidx.compose.runtime.Immutable
import com.barradev.chester.core.model.models.Customer

@Immutable
data class Step1SelectClientUiState(
    val customerList: List<Customer> = emptyList(),
    val searchQuery: String = "",
    val isSyncing: Boolean = true
)