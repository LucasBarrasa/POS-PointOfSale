package com.barradev.chester.core.model.models


data class Customer(
    val id: Long,
    val remoteId: Long,
    val fullName: String,
    val address: String,
    val phone: String,
    val hasCurrentAccount: Boolean,
    val isActive: Boolean
)

