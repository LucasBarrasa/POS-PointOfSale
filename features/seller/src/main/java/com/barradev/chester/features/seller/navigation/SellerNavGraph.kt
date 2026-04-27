package com.barradev.chester.features.seller.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.barradev.chester.features.seller.presentation.SellerContainerScreen
import com.barradev.chester.navigation.RootGraphDestinations


fun NavGraphBuilder.sellerNavGraph() {

    composable<RootGraphDestinations.SellerMainRoute> {
        SellerContainerScreen()
    }

}