package com.barradev.chester.features.seller.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface SellerDestinations {

    @Serializable data object Home : SellerDestinations
    @Serializable data object Customers : SellerDestinations
    @Serializable data object Products : SellerDestinations

}