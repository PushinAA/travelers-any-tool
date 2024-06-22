package com.nri.mycharacter.ui.page

import android.content.res.Resources
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nri.mycharacter.R
import com.nri.mycharacter.infra.service.ChangesetService
import com.nri.mycharacter.ui.navigation.MainAppRoutes
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun StartScreen(
    resources: Resources,
    onFinishPreparations: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val changesetService: ChangesetService = koinInject()

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        val job = scope.launch {
            changesetService.applyAll(resources)
        }
        job.join()
        onFinishPreparations.invoke()
    }

    // Image
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_monochrome),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
            contentDescription = "Logo",
            modifier = Modifier
                .size(500.dp)
                .scale(scale.value)
        )
        Text(text = stringResource(id = R.string.app_name), style = typography.displayMedium)
    }
}
