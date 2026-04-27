package com.barradev.chester.core.model.models

sealed class QuantityAddProduct {
    data class Delta(val quantityValue: Double): QuantityAddProduct()
    data class SetSpecific(val quantityValue: Double): QuantityAddProduct()
}