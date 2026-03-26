package ovh.motylek.outpostify.ui.screens.parcels

import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.api.data.ParcelStatus
import ovh.motylek.outpostify.data.database.entities.ParcelWithEvents
import ovh.motylek.outpostify.ui.common.theme.ExpressiveListItemShapes
import ovh.motylek.outpostify.ui.common.views.EmptyView
import ovh.motylek.outpostify.ui.viewmodels.parcels.ReceivedParcelsViewModel
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

enum class ApiTranslation(val resId: Int) {
    CREATED(R.string.CONFIRMED),
    SENT(R.string.DISPATCHED_BY_SENDER),
    TAKEN_BY_COURIER(R.string.TAKEN_BY_COURIER),
    ADOPTED_AT_SOURCE_BRANCH(R.string.ADOPTED_AT_SOURCE_BRANCH),
    SENT_FROM_SOURCE_BRANCH(R.string.SENT_FROM_SOURCE_BRANCH),
    OUT_FOR_DELIVERY(R.string.OUT_FOR_DELIVERY),
    READY_TO_PICKUP(R.string.READY_TO_PICKUP),
    DELIVERED(R.string.DELIVERED),
}

val apiTranslationMap: Map<String, Int> =
    ApiTranslation.entries.associate { it.name to it.resId }

@Composable
fun ReceivedParcelsScreen(
    viewModel: ReceivedParcelsViewModel = koinViewModel()
) {
    val parcels by viewModel.parcels.collectAsStateWithLifecycle()
    var selectedParcel by remember { mutableStateOf<ParcelWithEvents?>(null) }


    if (parcels.isEmpty()) {
        EmptyView(
            Icons.Default.LocalShipping,
            stringResource(R.string.Parcels_NoReceived)
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ParcelList(
                parcels,
                onParcelClick = { parcel ->
                    selectedParcel = parcel
                }
            )
        }
    }

    selectedParcel?.let { parcel ->
        ParcelSheet(
            parcel = parcel,
            onDismiss = { selectedParcel = null }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ParcelList(
    parcels: List<ParcelWithEvents>,
    onParcelClick: (ParcelWithEvents) -> Unit
) {
    if (parcels.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {

        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            itemsIndexed(parcels) { index, parcelwithevents ->
                val parcel = parcelwithevents.parcel

                val isFirst = index == 0
                val isLast = index == parcels.lastIndex
                val isOnly = parcels.count() == 1

                val shape = when {
                    isOnly -> ExpressiveListItemShapes.singleListItemShape
                    isFirst -> ExpressiveListItemShapes.topListItemShape
                    isLast -> ExpressiveListItemShapes.bottomListItemShape
                    else -> ExpressiveListItemShapes.middleListItemShape
                }

                val shapes = ListItemShapes(
                    shape = shape,
                    selectedShape = shape,
                    pressedShape = shape,
                    focusedShape = shape,
                    hoveredShape = shape,
                    draggedShape = shape
                )

                ListItem(
                    onClick = {
                        onParcelClick(parcelwithevents)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    supportingContent = {
                        Column {
                            Text(
                                text = stringResource(R.string.Parcels_Number),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = parcel.shipmentNumber,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = stringResource(R.string.Parcels_Sender),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = parcel.senderName ?: "???",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    },
                    shapes = shapes,
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column {
                        Text(
                            text = apiTranslationMap[parcelwithevents.events[0].type.name]
                                ?.let { stringResource(it) }
                                ?: "???",
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EventList(
    parcel: ParcelWithEvents,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        parcel.events.forEachIndexed { index, event ->

            val isFirst = index == 0
            val isLast = index == parcel.events.lastIndex
            val isOnly = parcel.events.count() == 1

            val formatter = LocalDateTime.Format {
                this@Format.day(padding = Padding.ZERO)
                char('.')
                monthNumber()
                char('.')
                year()
                char(' ')
                hour()
                char(':')
                minute()
            }

            val shape = when {
                isOnly -> ExpressiveListItemShapes.singleListItemShape
                isFirst -> ExpressiveListItemShapes.topListItemShape
                isLast -> ExpressiveListItemShapes.bottomListItemShape
                else -> ExpressiveListItemShapes.middleListItemShape
            }

            val shapes = ListItemShapes(
                shape = shape,
                selectedShape = shape,
                pressedShape = shape,
                focusedShape = shape,
                hoveredShape = shape,
                draggedShape = shape
            )

            ListItem(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                supportingContent = {
                    Text(
                        text = event.date.format(formatter),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                shapes = shapes,
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Text(
                    text = apiTranslationMap[event.type.name]
                        ?.let { stringResource(it) }
                        ?: "???",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ParcelSheet(
    parcel: ParcelWithEvents,
    onDismiss: () -> Unit
) {
    val padding = 16.dp
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues()
        .calculateBottomPadding() + padding
    val skipPartiallyExpanded = false
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    bottom = bottomPadding,
                    top = padding,
                    start = padding,
                    end = padding
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Basic Data
            Box {
                Column {
                    // Title
                    Text(
                        text = apiTranslationMap[parcel.events[0].type.name]
                            ?.let { stringResource(it) }
                            ?: "???",
                        modifier = Modifier.padding(bottom = 24.dp),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp),
                    )

                    // Content
                    Text(
                        text = stringResource(R.string.Parcels_Number),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = parcel.parcel.shipmentNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = stringResource(R.string.Parcels_Sender),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = parcel.parcel.senderName ?: "???",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            // Parcel collect data (if exists)
            if ((!parcel.parcel.pickupData_openCode.isNullOrBlank()) and (parcel.events[0].type != ParcelStatus.DELIVERED)) {
                HorizontalDivider()

                Box {
                    Column {
                        // QR Code
                        var qrExpanded by remember { mutableStateOf(false) }
                        val qrPadding by animateDpAsState(
                            targetValue = if (qrExpanded) 24.dp else 128.dp,
                            label = "qrPadding"
                        )

                        Row(
                            modifier = Modifier
                                .padding(start = qrPadding, end = qrPadding)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                onClick = { qrExpanded = !qrExpanded },
                                colors = CardColors(
                                    Color.White,
                                    Color.White,
                                    Color.White,
                                    Color.White
                                )
                            ) {
                                parcel.parcel.pickupData_openQrCode?.let {
                                    Image(
                                        modifier = Modifier
                                            .padding(24.dp)
                                            .fillMaxWidth(),
                                        painter = rememberQrCodePainter(it),
                                        contentScale = ContentScale.FillWidth,
                                        contentDescription = "QR Code used for collecting the parcel"
                                    )
                                }
                            }
                        }

                        // Time left countdown slider or sum
                        Row {
                            parcel.parcel.pickupData_storedOn?.let {
                                parcel.parcel.pickupData_storedTo?.let { target ->
                                    CountdownProgressBar(
                                        target,
                                        it,
                                        Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }

                        // Rest
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.Parcels_OpenCode),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                parcel.parcel.pickupData_openCode?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.Parcels_StoredOn),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                Text(
                                    text = "${
                                        parcel.parcel.pickupData_storedOn?.toJavaLocalDateTime()
                                            ?.format(DateTimeFormatter.ofPattern("dd MMM HH:mm"))
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = stringResource(R.string.Parcels_TimeLeft),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Right
                                )
                                parcel.parcel.pickupData_storedTo?.let { CountdownText(it) }

                                Text(
                                    text = stringResource(R.string.Parcels_PickupBefore),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Right,
                                )
                                Text(
                                    text = "${
                                        parcel.parcel.pickupData_storedTo?.toJavaLocalDateTime()
                                            ?.format(DateTimeFormatter.ofPattern("dd MMM HH:mm"))
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Right,
                                )
                            }
                        }


                    }
                }
            }

            HorizontalDivider()

            // Pickup data
            Box {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.Parcels_PickupPlace),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = parcel.parcel.pickupData_name ?: "???",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = stringResource(R.string.Parcels_PickupAddress),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = parcel.parcel.pickupData_address ?: "???",
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Text(
                                text = (parcel.parcel.pickupData_city)
                                    ?: "???", // TODO: Also show postcode if anyone cares
                                style = MaterialTheme.typography.bodyMedium,

                                )

                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = stringResource(R.string.Parcels_Availability),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Right
                            )
                            Text(
                                text = parcel.parcel.pickupData_availability
                                    ?: "???",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp),
                                textAlign = TextAlign.Right
                            )

                            Text(
                                text = stringResource(R.string.Parcels_Location),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Right
                            )
                            Text(
                                text = parcel.parcel.pickupData_location
                                    ?: "???",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Right
                            )
                        }
                    }

                    Row(
                        Modifier.padding(top = 8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val uri = "geo:0,0?q=${parcel.parcel.pickupData_latitude},${parcel.parcel.pickupData_longitude}".toUri()
                                val intent = Intent(Intent.ACTION_VIEW, uri)

                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Filled.Navigation,
                                contentDescription = "",
                                modifier = Modifier.size(
                                    ButtonDefaults.iconSizeFor(
                                        ButtonDefaults.MinHeight
                                    )
                                ),
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MinHeight)))
                            Text(stringResource(R.string.Parcels_ShowOnMap))
                        }
                    }
                }
            }

            HorizontalDivider()

            // Event List
            Box {
                Column(
                    Modifier.padding(top = 8.dp)
                ) {
                    EventList(parcel)
                }
            }
        }
    }
}

@Composable
fun CountdownText(
    targetTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    var remainingTime by remember { mutableStateOf(Duration.ZERO) }

    fun calculateRemaining(): Duration {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val secondsLeft = targetTime.toInstant(TimeZone.currentSystemDefault())
            .epochSeconds - now.toInstant(TimeZone.currentSystemDefault()).epochSeconds
        return if (secondsLeft > 0) secondsLeft.toDuration(DurationUnit.SECONDS) else Duration.ZERO
    }

    LaunchedEffect(targetTime) {
        while (true) {
            remainingTime = calculateRemaining()
            if (remainingTime == Duration.ZERO) break
            delay(1000)
        }
    }

    val displayText = run {
        val days = remainingTime.inWholeDays
        val hours = (remainingTime.inWholeHours % 24)
        val minutes = (remainingTime.inWholeMinutes % 60)
        val seconds = (remainingTime.inWholeSeconds % 60)

        if (days > 0) {
            "%dd %02d:%02d:%02d".format(days, hours, minutes, seconds)
        } else {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        }
    }

    Text(
        text = displayText,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Right
    )
}


@Composable
fun CountdownProgressBar(
    target: LocalDateTime,
    start: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    val tz = TimeZone.currentSystemDefault()
    val totalSecs = remember { (target.toInstant(tz) - start.toInstant(tz)).inWholeSeconds.coerceAtLeast(1) }

    val tickMs = remember {
        when {
            totalSecs > 2 * 24 * 3600 -> 60_000L  // > 2 days  → every minute
            totalSecs > 2 * 3600       -> 10_000L  // > 2 hours → every 10s
            else                       ->  1_000L  // short     → every second
        }
    }

    var now by remember { mutableStateOf(Clock.System.now()) }
    LaunchedEffect(tickMs) { while (true) { delay(tickMs); now = Clock.System.now() } }

    val remaining = (target.toInstant(tz) - now).inWholeSeconds.coerceAtLeast(0)
    val progress by animateFloatAsState(remaining / totalSecs.toFloat(), tween(900))

    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.fillMaxWidth()
    )
}
