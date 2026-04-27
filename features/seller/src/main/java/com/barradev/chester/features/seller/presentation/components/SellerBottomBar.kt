package com.barradev.chester.features.seller.presentation.components


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.barradev.chester.features.seller.navigation.SellerDestinations


@Composable
fun SellerBottomBar(
    navController: NavHostController
) {
    // Definición de ítems
    val items = listOf(
        BottomNavItem(
            route = SellerDestinations.Home,
            icon = Icons.Default.Home,
            label = "Inicio"
        ),
        BottomNavItem(
            route = SellerDestinations.Products,
            icon = Icons.AutoMirrored.Default.List,
            label = "Catálogo"
        ),
        BottomNavItem(
            route = SellerDestinations.Customers,
            icon = Icons.Default.Person,
            label = "Clientes"
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            // Lógica para saber si está seleccionado (incluyendo jerarquía)
            val isSelected = currentDestination?.hierarchy?.any { destination ->
                destination.hasRoute(item.route::class)
            } == true

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Limpia la pila hasta el inicio del grafo del vendedor
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: SellerDestinations,
    val icon: ImageVector,
    val label: String
)