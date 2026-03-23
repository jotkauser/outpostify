package ovh.motylek.outpostify.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import ovh.motylek.outpostify.ui.common.components.ScreenScope
import ovh.motylek.outpostify.ui.routes.Route
import ovh.motylek.outpostify.ui.screens.addaccount.AddAccountScreen
import ovh.motylek.outpostify.ui.screens.parcels.ParcelsScreen
import ovh.motylek.outpostify.ui.screens.welcome.WelcomeScreen

internal fun EntryProviderScope<Route>.screens(
    screenScope: ScreenScope,
    backStack: MutableList<Route>,
    avatar: @Composable () -> Unit
) {
    entry<Route.Welcome> {
        WelcomeScreen(
            args = it,
            onNavigate = {
                backStack.add(Route.AddAccount)
            }
        )
    }

    entry<Route.AddAccount> {
        AddAccountScreen(
            args = it,
            scope = screenScope,
            onSuccess = {
                backStack.clear()
                backStack.add(Route.Parcels)
            }
        )
    }

    entry<Route.Parcels> {
        ParcelsScreen(
            args = it,
            scope = screenScope,
            avatar = avatar
        )
    }
}