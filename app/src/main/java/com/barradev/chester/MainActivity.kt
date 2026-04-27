package com.barradev.chester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.barradev.chester.features.createorder.presentation.createOrderGraph
import com.barradev.chester.features.orderdetail.orderDetailGraph
import com.barradev.chester.features.seller.navigation.sellerNavGraph
import com.barradev.chester.navigation.NavigationIntent
import com.barradev.chester.navigation.Navigator
import com.barradev.chester.navigation.RootGraphDestinations
import com.barradev.chester.ui.theme.ChesterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ChesterTheme {
                // NavController PADRE (Global)
                val rootNavController = rememberNavController()

                // Efectos de navegacion de CORE
                NavigationEffect(
                    navigationChannel = navigator.navigationActions,
                    navHostController = rootNavController
                )

                NavHost(
                    navController = rootNavController,
                    // Saltamos Auth
                    startDestination = RootGraphDestinations.SellerMainRoute
                ) {
                    // GrafoSeller
                    sellerNavGraph()

                    createOrderGraph()

                    orderDetailGraph()


                    // Futuros Graphos
                    // authGraph()
                    // adminGraph()
                    // deliveryGraph()

                    //createEditClientGraph()
                    //createEditProductGraph()
                }
            }
        }
    }
}


@Composable
fun NavigationEffect(
    navigationChannel: Flow<NavigationIntent>,
    navHostController: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner, navigationChannel) {
        // Recoleccion de eventos con seguridad de ciclo de vida
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigationChannel.collect { intent ->
                if (navHostController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    when (intent) {
                        is NavigationIntent.NavigateBack -> {
                            navHostController.popBackStack()
                        }
                        is NavigationIntent.NavigateTo -> {
                            navHostController.navigate(intent.route) {
                                launchSingleTop = intent.launchSingleTop

                                intent.popUpToRoute?.let { popRoute ->
                                    popUpTo(popRoute) {
                                        inclusive = intent.inclusive
                                    }
                                }
                            }
                        }
                        is NavigationIntent.NavigateToRoot -> {
                            navHostController.navigate(intent.route) {
                                popUpTo(navHostController.graph.id) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }
}
