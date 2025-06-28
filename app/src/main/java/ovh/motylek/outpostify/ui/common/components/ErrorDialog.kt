package ovh.motylek.outpostify.ui.common.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import ovh.motylek.outpostify.R

@Composable
fun ErrorDialog(
    exception: Throwable,
    onDismiss: () -> Unit
) {
    val stackTrace = exception.stackTraceToString()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.Generic_Error)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.Generic_Ok))
            }
        },
        text = {
            val scrollState = rememberScrollState()
            val scrollState2 = rememberScrollState()
            Box(modifier = Modifier
                .verticalScroll(scrollState)
                .horizontalScroll(scrollState2)
            ) {
                Column {
                    Text(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        text = stackTrace
                    )
                }
            }
        },
    )
}