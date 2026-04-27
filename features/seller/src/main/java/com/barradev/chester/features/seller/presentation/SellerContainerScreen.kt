package com.barradev.chester.features.seller.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barradev.chester.features.seller.presentation.components.SellerBottomBar
import com.barradev.chester.features.seller.presentation.customers.CustomerRoute
import com.barradev.chester.features.seller.presentation.home.HomeSellerRoute
import com.barradev.chester.features.seller.presentation.products.ProductsRoute
import com.barradev.chester.features.seller.navigation.SellerDestinations

@Composable
fun SellerContainerScreen() {
    val nestedNavController = rememberNavController()


    Scaffold(
        bottomBar = {
            SellerBottomBar(navController = nestedNavController)
        }) { innerPadding ->


        NavHost(
            navController = nestedNavController,
            startDestination = SellerDestinations.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<SellerDestinations.Home> {
                HomeSellerRoute()
            }

            composable<SellerDestinations.Products> {
                ProductsRoute()
            }

            composable<SellerDestinations.Customers> {
                CustomerRoute()
            }

        }
    }
}