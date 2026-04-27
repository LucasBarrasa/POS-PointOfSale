package com.barradev.chester.features.seller.presentation.home

import com.barradev.chester.core.model.models.OrderSummary
import com.barradev.chester.features.seller.presentation.home.model.OrderUiModel


sealed interface HomeSellerStateUi {
    object Loading : HomeSellerStateUi
    data class Success(val orders: List<OrderUiModel>) : HomeSellerStateUi
    data class Error(val message: String) : HomeSellerStateUi
}