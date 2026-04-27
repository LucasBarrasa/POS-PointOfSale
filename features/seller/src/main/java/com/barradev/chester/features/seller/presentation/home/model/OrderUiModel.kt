package com.barradev.chester.features.seller.presentation.home.model

import androidx.compose.ui.graphics.Color

data class OrderUiModel(
    val id: Long,
    val title: String,
    val date: String,
    val itemsCount: Double,
    val totalAmount: String,
    val statusLabel: String,
    val statusColor: Color,
    val paymentMethod: String
)
