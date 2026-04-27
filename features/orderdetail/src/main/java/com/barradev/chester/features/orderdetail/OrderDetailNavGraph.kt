package com.barradev.chester.features.orderdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.barradev.chester.features.orderdetail.presentation.OrderDetailRoute
import com.barradev.chester.navigation.RootGraphDestinations

fun NavGraphBuilder.orderDetailGraph(){

    composable<RootGraphDestinations.OrderDetail> {
        OrderDetailRoute()
    }
}