package ovh.motylek.outpostify.ui.common.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope


data class ScreenScope @OptIn(ExperimentalSharedTransitionApi::class) constructor(
    val sharedTransitionScope: SharedTransitionScope,
)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    modifier: Modifier = Modifier,
    scope: ScreenScope,
    topBar: @Composable (modifier: Modifier) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    with(scope.sharedTransitionScope) {
        Scaffold(
            modifier = modifier,
            topBar = {
                topBar(
                    Modifier.sharedElement(
                        sharedContentState = scope.sharedTransitionScope.rememberSharedContentState(key = "appBar"),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateLeftPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateRightPadding(LocalLayoutDirection.current),
                        bottom = 0.dp
                    )
            ) {
                content()
            }
        }
    }
}
