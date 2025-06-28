package ovh.motylek.outpostify.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.routes.AddAccount
import ovh.motylek.outpostify.ui.routes.Welcome
import ovh.motylek.outpostify.ui.screens.AddAccountScreen
import ovh.motylek.outpostify.ui.screens.WelcomeScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    var currentScreen by remember { mutableStateOf<Any?>(Welcome) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            Welcome -> stringResource(R.string.app_name)
                            AddAccount -> stringResource(R.string.Title_AddAccount)
                            else -> stringResource(R.string.app_name)
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Welcome,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Welcome> {
                currentScreen = it.toRoute<Welcome>()
                WelcomeScreen(navController)
            }

            composable<AddAccount> {
                currentScreen = it.toRoute<AddAccount>()
                AddAccountScreen()
            }
        }
    }
}