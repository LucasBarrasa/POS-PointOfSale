package com.barradev.chester.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NavigatorImpl @Inject constructor() : Navigator {

    // Channel.BUFFERED para no perder eventos si la UI no está lista
    private val _navigationActions = Channel<NavigationIntent>(Channel.BUFFERED)

    override val navigationActions: Flow<NavigationIntent>
        get() = _navigationActions.receiveAsFlow()

    override fun navigateTo(route: Any, popUpToRoute: Any?, inclusive: Boolean) {
        _navigationActions.trySend(
            NavigationIntent.NavigateTo(
                route,
                popUpToRoute = popUpToRoute,
                inclusive = inclusive
            )
        )
    }

    override fun navigateBack() {
        _navigationActions.trySend(NavigationIntent.NavigateBack)
    }

    override fun navigateToRoot(route: Any) {
        _navigationActions.trySend(NavigationIntent.NavigateTo(
            route,
            popUpToRoute = null,
            inclusive = true
        ))
    }


}

