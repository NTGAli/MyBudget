package com.ntg.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.ntg.core.mybudget.common.logd

@Composable
fun Lottie(
    modifier: Modifier = Modifier,
    res: Int,
    color: Color? = null,
    isPlaying: MutableState<Boolean> = remember { mutableStateOf(true) },
    restartOnPlay: MutableState<Boolean> = remember { mutableStateOf(true) },
    iterations: Int = LottieConstants.IterateForever,
    onProgressChange:(Float) -> Unit =  {}
){

    var currentProgress by remember { mutableFloatStateOf(0f) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            res
        )
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = iterations,
        isPlaying = isPlaying.value,
        restartOnPlay = restartOnPlay.value,

    )


    LaunchedEffect(progress) {
        currentProgress = progress
//        if (!restartOnPlay.value) currentProgress + 0.8f
        onProgressChange(progress)
    }

    if (color != null){
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color.hashCode(),
                    BlendModeCompat.SRC_ATOP
                ),
                keyPath = arrayOf(
                    "**"
                )
            )
        )
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            progress = { currentProgress },
            dynamicProperties = dynamicProperties
        )
    }else{
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            progress = { progress },
        )
    }

}