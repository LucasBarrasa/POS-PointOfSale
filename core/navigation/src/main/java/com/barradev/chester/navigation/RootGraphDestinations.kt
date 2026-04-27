package com.barradev.chester.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface RootGraphDestinations {

    @Serializable
    data object SellerMainRoute : RootGraphDestinations

    // Otras rutas

    @Serializable
    data class CreateOrderRoute(
        val preSelectedClientId: Long? = null
    ) : RootGraphDestinations

    @Serializable
    data class OrderDetail(
        val orderId: Long
    ) : RootGraphDestinations

}