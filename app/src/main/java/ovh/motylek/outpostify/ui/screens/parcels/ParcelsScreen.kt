package ovh.motylek.outpostify.ui.screens.parcels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.views.EmptyView
import ovh.motylek.outpostify.ui.routes.Parcels
import ovh.motylek.outpostify.ui.viewmodels.parcels.ParcelsViewModel
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelsScreen(
    args: Parcels,
    viewModel: ParcelsViewModel = koinViewModel()
) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val tabs = listOf(
        stringResource(R.string.Parcels_Received),
        stringResource(R.string.Parcels_Sent),
        stringResource(R.string.Parcels_Returned)
    )
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { coroutineScope.launch { viewModel.refreshParcels() } },
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    HorizontalPager(pagerState, modifier = Modifier.fillParentMaxHeight()) { page ->
                        when (page) {
                            0 -> {
                                ReceivedParcelsScreen(args)
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
    }
}