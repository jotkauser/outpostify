package ovh.motylek.outpostify.ui.screens.addaccount

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ovh.motylek.outpostify.R
import ovh.motylek.outpostify.ui.common.components.ScreenScaffold
import ovh.motylek.outpostify.ui.common.components.ScreenScope
import ovh.motylek.outpostify.ui.common.views.EmptyView
import ovh.motylek.outpostify.ui.routes.Route
import ovh.motylek.outpostify.ui.viewmodels.addaccount.AddAccountUiState
import ovh.motylek.outpostify.ui.viewmodels.addaccount.AddAccountViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AddAccountScreen(
    args: Route.AddAccount,
    scope: ScreenScope,
    onSuccess: () -> Unit,
    viewModel: AddAccountViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    ScreenScaffold(
        scope = scope,
        topBar = { modifier ->
            TopAppBar(
                title = { Text(text = stringResource(R.string.Title_AddAccount)) },
                modifier = modifier
            )
        },
    ) {
        when (val uiState = state) {
            is AddAccountUiState.WebViewDisplaying -> {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    return false
                                }

                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    val uri = Uri.parse(url)
                                    val code = uri.getQueryParameter("code")
                                    if (code != null) {
                                        view?.stopLoading()
                                        coroutineScope.launch {
                                            viewModel.handleOauthLogin(url!!)
                                        }
                                    }
                                }
                            }
                            loadUrl(viewModel.getOauthUrl())

                        }
                    }
                )
            }
            is AddAccountUiState.Loading -> {
                EmptyView {
                    val stroke = Stroke(
                        width = with(LocalDensity.current) { 6.dp.toPx() },
                        cap = StrokeCap.Round
                    )
                    CircularWavyProgressIndicator(
                        modifier = Modifier.padding(16.dp).size(128.dp),
                        trackStroke = stroke,
                        stroke = stroke
                    )
                }
            }
            is AddAccountUiState.Error -> {
                EmptyView(
                    icon = painterResource(R.drawable.ic_block),
                    text = uiState.error.toString()
                )
            }
            is AddAccountUiState.Success -> {
                onSuccess()
            }
        }
    }
}