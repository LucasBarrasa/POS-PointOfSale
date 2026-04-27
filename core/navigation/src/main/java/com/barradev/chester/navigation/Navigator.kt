package com.barradev.chester.navigation

import kotlinx.coroutines.flow.Flow

interface Navigator {

    val navigationActions: Flow<NavigationIntent>

    fun navigateTo(
        route: Any,
        popUpToRoute: Any? = null,
        inclusive: Boolean = false
    )

    fun navigateBack()
    fun navigateToRoot(route: Any)

}

