package com.barradev.chester.features.createorder.presentation

import androidx.annotation.Keep
import kotlinx.serialization.Serializable


@Serializable
sealed interface CreateOrderDestinations {

    @Serializable data object Step1SelectClient: CreateOrderDestinations
    @Serializable data class Step2SelectProducts(val orderId: Long) : CreateOrderDestinations
    @Serializable data class Step3ConfirmOrder (val orderId: Long): CreateOrderDestinations

}