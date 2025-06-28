package ovh.motylek.outpostify.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.components.Avatar
import ovh.motylek.outpostify.ui.routes.AddAccount
import ovh.motylek.outpostify.ui.routes.Parcels
import ovh.motylek.outpostify.ui.routes.Welcome
import ovh.motylek.outpostify.ui.screens.AddAccountScreen
import ovh.motylek.outpostify.ui.screens.ParcelsScreen
import ovh.motylek.outpostify.ui.screens.WelcomeScreen
import ovh.motylek.outpostify.ui.viewmodels.NavigationViewModel
import kotlin.reflect.KClass

private data class NavigationItem(
    val label: Int,
    val iconSelected: ImageVector,
    val icon: ImageVector,
    val route: KClass<*>
)

private val navigationItems = listOf(
    NavigationItem(
        label = R.string.Navigation_Parcels,
        iconSelected = Icons.Default.LocalShipping,
        icon = Icons.Outlined.LocalShipping,
        route = Parcels::class
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    viewModel: NavigationViewModel = koinViewModel()
) {
    var currentScreen by remember { mutableStateOf<Any?>(Welcome) }
    val noBottomNavScreens = listOf(Welcome, AddAccount)
    val navController = rememberNavController()
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    val accountSwitcherState by viewModel.userSwtitcherVisible.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            is Welcome -> stringResource(R.string.app_name)
                            is AddAccount -> stringResource(R.string.Title_AddAccount)
                            is Parcels -> stringResource(R.string.Navigation_Parcels)
                            else -> stringResource(R.string.app_name)
                        }
                    )
                },
                actions = {
                    if (currentScreen !in noBottomNavScreens) {
                        Avatar(Modifier.padding(horizontal = 8.dp).clickable { viewModel.showUserSwitcher() }) {
                            Icon(Icons.Default.Person, null)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentScreen !in noBottomNavScreens) {
                NavigationBar {
                    navigationItems.forEach { item ->
                        val selected = currentScreen!!::class == item.route
                        NavigationBarItem(
                            selected = selected,
                            icon = { Icon(imageVector = if (selected) item.iconSelected else item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.label)) },
                            onClick = {
                                if (selected) return@NavigationBarItem
                                when (item.route) {
                                    Parcels::class -> navController.navigate(Parcels(userId.toLong()))
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = viewModel.getStartDestination(),
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Welcome> {
                currentScreen = it.toRoute<Welcome>()
                WelcomeScreen(navController)
            }

            composable<AddAccount> {
                currentScreen = it.toRoute<AddAccount>()
                AddAccountScreen(navController)
            }

            composable<Parcels> {
                currentScreen = it.toRoute<Parcels>()
                val args = it.toRoute<Parcels>()
                ParcelsScreen(args)
            }
        }
        if (accountSwitcherState) {
            BasicAlertDialog(
                onDismissRequest = { viewModel.hideUserSwitcher()}
            ) {
                Surface(
                    shape = AlertDialogDefaults.shape,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        accounts.forEach {
                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        if (it.id == userId.toLong()) {
                                            viewModel.hideUserSwitcher()
                                        } else {
                                            coroutineScope.launch {
                                                viewModel.switchUser(it)
                                                navController.navigate(Parcels(it.id))
                                            }
                                        }
                                    }
                                ) {
                                    Avatar(Modifier.padding(horizontal = 8.dp)) {
                                        Icon(Icons.Default.Person, null)
                                    }
                                    Text(it.phoneNumber, fontWeight = if (it.id == userId.toLong()) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                        item {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(
                                    onClick = {
                                        viewModel.hideUserSwitcher()
                                        navController.navigate(AddAccount)
                                    }
                                ) {
                                    Text(stringResource(R.string.AddAccount_NewAccount))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}