package com.barradev.chester.features.createorder.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.barradev.chester.features.createorder.presentation.steps.step1SelectClient.Step1SelectClientRoute
import com.barradev.chester.features.createorder.presentation.steps.step2SelectProducts.Step2SelectProductsRoute
import com.barradev.chester.features.createorder.presentation.steps.step3CheckoutOrder.Step3ConfirmOrderRoute
import com.barradev.chester.navigation.RootGraphDestinations

fun NavGraphBuilder.createOrderGraph() {

    navigation<RootGraphDestinations.CreateOrderRoute>(
        startDestination = CreateOrderDestinations.Step1SelectClient
    ) {

        composable<CreateOrderDestinations.Step1SelectClient> {
            Step1SelectClientRoute()
        }

        composable<CreateOrderDestinations.Step2SelectProducts> {
            Step2SelectProductsRoute()
        }

        composable<CreateOrderDestinations.Step3ConfirmOrder> {
            Step3ConfirmOrderRoute()
        }

    }
}