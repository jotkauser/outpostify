package ovh.motylek.outpostify.ui.screens.parcels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.views.EmptyView
import ovh.motylek.outpostify.ui.viewmodels.parcels.ReceivedParcelsViewModel

@Composable
fun ReceivedParcelsScreen(
    viewModel: ReceivedParcelsViewModel = koinViewModel()
) {
    val parcels by viewModel.parcels.collectAsStateWithLifecycle()

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
    ) {
        if (parcels.isEmpty()) {
            EmptyView(
                Icons.Default.LocalShipping,
                stringResource(R.string.Parcels_NoReceived)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(parcels) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Text(
                                text = it.events[0].type.name,
                                modifier = Modifier.padding(bottom = 8.dp),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = stringResource(R.string.Parcels_Number),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = it.parcel.shipmentNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = stringResource(R.string.Parcels_Sender),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = it.parcel.senderName ?: "???",
                                style = MaterialTheme.typography.bodyMedium,
                            )

                        }
                    }
                }
            }
        }
    }
}