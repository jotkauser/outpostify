package ovh.motylek.outpostify

import android.content.ClipboardManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ovh.motylek.outpostify.ui.theme.OutPostifyTheme

class CrashActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val stacktrace = intent.extras!!.getString("exception")!!
        val clipboardService = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        setContent {
            OutPostifyTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.FatalError_Title))},
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = false,
                                onClick = {
                                    clipboardService.setPrimaryClip(
                                        android.content.ClipData.newPlainText(
                                            "stacktrace",
                                            stacktrace
                                        )
                                    )
                                },
                                label = { Text(stringResource(R.string.FatalError_Copy)) },
                                icon = { Icon(Icons.Default.ContentCopy, null) }
                            )
                            NavigationBarItem(
                                selected = false,
                                onClick = { finish() },
                                label = { Text(stringResource(R.string.FatalError_Exit)) },
                                icon = { Icon(Icons.Default.Close, null) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = CardDefaults.shape,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState()).horizontalScroll(rememberScrollState())
                            ) {
                                Text(fontSize = 12.sp, lineHeight = 12.sp, text = stacktrace)
                            }
                        }
                    }
                }
            }
        }
    }
}