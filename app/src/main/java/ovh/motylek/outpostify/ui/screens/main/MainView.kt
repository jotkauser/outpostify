package ovh.motylek.outpostify.ui.screens.main

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.PolymorphicSerializer
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.components.Avatar
import ovh.motylek.outpostify.ui.common.components.ScreenScope
import ovh.motylek.outpostify.ui.routes.Route
import ovh.motylek.outpostify.ui.routes.routesSerializer
import ovh.motylek.outpostify.ui.viewmodels.main.MainViewModel
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    viewModel: MainViewModel = koinViewModel()
) {
    val showAccountPicker by viewModel.userSwitcherVisible.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val currentAccount by viewModel.currentAccount.collectAsStateWithLifecycle()

    val backStack = rememberSerializable(
        configuration = SavedStateConfiguration { serializersModule = routesSerializer },
        serializer = SnapshotStateListSerializer(
            PolymorphicSerializer(Route::class)
        ),
    ) {
        runBlocking { mutableStateListOf(viewModel.getStartDestination()) }
    }

    val currentRoute = backStack.lastOrNull()
    val coroutineScope = rememberCoroutineScope()

    val showBottomNav = currentRoute != Route.Welcome && currentRoute != Route.AddAccount

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    navigationItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            label = { Text(stringResource(item.label)) },
                            icon = { Icon(imageVector = if (selected) item.iconSelected else item.icon, contentDescription = null) },
                            onClick = { backStack.add(item.route) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        ScreenScopeWrapper { screenScope ->
            NavDisplay(
                modifier = Modifier.fillMaxSize().padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else 0.dp),
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {

                    val avatar = @Composable {
                        Avatar(Modifier.padding(horizontal = 8.dp).clickable { viewModel.showUserSwitcher() }) {
                            Icon(Icons.Default.Person, null)
                        }
                    }

                    screens(screenScope, backStack, avatar)
                }
            )
            if (showAccountPicker) {
                BasicAlertDialog(
                    onDismissRequest = { viewModel.hideUserSwitcher() }
                ) {
                    Surface(
                        shape = AlertDialogDefaults.shape,
                        tonalElevation = AlertDialogDefaults.TonalElevation,
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(accounts) { _, account ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        if (account.id == currentAccount?.id) {
                                            viewModel.hideUserSwitcher()
                                        } else {
                                            coroutineScope.launch {
                                                viewModel.switchUser(account)
                                            }
                                        }
                                    }
                                ) {
                                    Avatar(Modifier.padding(horizontal = 8.dp)) {
                                        Icon(Icons.Default.Person, null)
                                    }
                                    Text(account.phoneNumber, fontWeight = if (account.id == currentAccount?.id) FontWeight.Bold else FontWeight.Normal)
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
                                            backStack.add(Route.AddAccount)
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
}

private data class NavigationItem(
    val label: Int,
    val iconSelected: ImageVector,
    val icon: ImageVector,
    val route: Route
)

private val navigationItems = listOf(
    NavigationItem(
        label = R.string.Navigation_Parcels,
        iconSelected = Icons.Default.LocalShipping,
        icon = Icons.Outlined.LocalShipping,
        route = Route.Parcels
    ),
)

@Composable
fun ScreenScopeWrapper(
    block: @Composable (ScreenScope) -> Unit
) {
    SharedTransitionLayout {
        block(ScreenScope(this@SharedTransitionLayout))
    }
}