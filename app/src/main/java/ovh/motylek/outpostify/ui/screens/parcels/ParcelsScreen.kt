package ovh.motylek.outpostify.ui.screens.parcels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.views.EmptyView
import ovh.motylek.outpostify.ui.common.components.ScreenScaffold
import ovh.motylek.outpostify.ui.common.components.ScreenScope
import ovh.motylek.outpostify.ui.routes.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelsScreen(
    args: Route.Parcels,
    scope: ScreenScope,
    avatar: @Composable () -> Unit,
) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf(
        stringResource(R.string.Parcels_Received),
        stringResource(R.string.Parcels_Sent),
        stringResource(R.string.Parcels_Returned)
    )
    ScreenScaffold(
        scope = scope,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.Navigation_Parcels))
                },
                actions = { avatar() },
                modifier = it
            )
        }
    ) {
        Column {
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                tabs = {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            text = { Text(text = title) }
                        )
                    }
                }
            )
            HorizontalPager(pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> {
                        ReceivedParcelsScreen()
                    }

                    1 -> {
                        EmptyView(
                            Icons.Default.LocalShipping,
                            stringResource(R.string.Parcels_NoSent)
                        )
                    }

                    2 -> {
                        EmptyView(
                            Icons.Default.LocalShipping,
                            stringResource(R.string.Parcels_NoReturned)
                        )
                    }
                }
            }
        }
    }

}