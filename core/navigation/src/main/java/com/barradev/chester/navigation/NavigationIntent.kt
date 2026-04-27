package com.barradev.chester.navigation


sealed interface NavigationIntent {

    data class NavigateTo(
        val route: Any,
        val popUpToRoute: Any? = null,
        val inclusive: Boolean = false,
        val launchSingleTop: Boolean = true,
    ) : NavigationIntent

    data object NavigateBack : NavigationIntent
    data class NavigateToRoot(val route: Any) : NavigationIntent

}