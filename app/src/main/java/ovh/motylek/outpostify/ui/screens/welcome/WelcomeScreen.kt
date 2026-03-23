package ovh.motylek.outpostify.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.DrawablePainter
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.routes.Route

@Composable
fun WelcomeScreen(
    args: Route.Welcome,
    onNavigate: () -> Unit,
) {
    val context = LocalContext.current
    val icon = context.packageManager.getApplicationIcon(context.packageName)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = DrawablePainter(icon),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(128.dp)
        )
        Text(
            text = "${stringResource(R.string.Welcome_Title)} ${stringResource(R.string.app_name)}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        OutlinedButton(onClick = {
            onNavigate()
        }, modifier = Modifier.fillMaxWidth().padding(horizontal = 90.dp)) {
            Text(
                text = stringResource(R.string.Welcome_Button),
            )
        }

    }
}