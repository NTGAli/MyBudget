package com.ntg.core.designsystem.components


import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun OtpField(
    modifier: Modifier = Modifier,
    otpText: String = "12345",
    otpCount: Int = 5,
    wasWrong: Boolean,
    isSucceeded: Boolean,
    isLoading: Boolean,
    defaultBorderColor: Color = MaterialTheme.colorScheme.surfaceDim,
    errorColor: Color = MaterialTheme.colorScheme.error,
    successColor: Color = MaterialTheme.colorScheme.secondary,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("ERR ::: INVALID OtpCount. correct : $otpCount but ${otpText.length}")
        }
    }

    var itemSelected by remember {
        mutableStateOf(false)
    }

    var posSelected by remember {
        mutableIntStateOf(0)
    }

    var selection by remember {
        mutableStateOf(TextRange(otpText.length))
    }

    var currentText by remember {
        mutableStateOf("")
    }

    val buttonRotate = remember { Animatable(0f) }

    LaunchedEffect(wasWrong) {
        buttonRotate.animateTo(0f, keyframes {
            durationMillis = 250
            0f at 0
            -15f at 150
        })
    }

    val color = remember { Animatable(defaultBorderColor) }

    if (wasWrong) {
        currentText = ""
        onOtpTextChange.invoke("", false)
        itemSelected = false
    }
    LaunchedEffect(currentText) {
        if (wasWrong && otpText.length == otpCount) {
            color.animateTo(errorColor, animationSpec = spring(2f))
            color.animateTo(defaultBorderColor, animationSpec = spring(2f))
        } else {
            color.animateTo(defaultBorderColor, animationSpec = spring(2f))
        }
    }


    selection = if (itemSelected) {
        TextRange(posSelected, posSelected + 1)
    } else {
        TextRange(otpText.length)
    }

    val focusRequester = remember { FocusRequester() }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BasicTextField(
            modifier = modifier
                .focusRequester(focusRequester)
                .offset(x = buttonRotate.value.dp),
            value = TextFieldValue(currentText, selection = selection),
            onValueChange = {
                if (!isLoading){
                    if (it.text.length <= otpCount) {
                        onOtpTextChange.invoke(it.text, it.text.length == otpCount)

                    } else {
                        itemSelected = false
                    }

                    if (it.text.length >= currentText.length) {
                        currentText = it.text
                        posSelected++
                    } else {
                        if (currentText.replace("_", "").dropLast(1) == it.text.replace("_", "")) {
                            if (currentText.last() == '_') currentText = currentText.dropLast(1)
                            currentText = it.text
                            itemSelected = false
                        } else {
                            currentText = formatInput(currentText, it.text)
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = currentText,
                            isFocused = if (itemSelected) {
                                posSelected == index
                            } else {
                                (currentText.length) == index
                            },
                            color,
                            defaultBorderColor,
                            successColor,
                            isSucceeded,
                            isLoading
                        ) {
                            itemSelected = true
                            posSelected = it
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            },)

        LaunchedEffect(focusRequester) {
            focusRequester.requestFocus()

        }
    }
}

@Composable
private fun CharView(
    index: Int,
    text: String,
    isFocused: Boolean,
    color: Animatable<Color, AnimationVector4D>,
    defaultColor: Color,
    successColor: Color,
    isSuccess: Boolean,
    isLoading: Boolean,
    onClick: (Int) -> Unit
) {

    var boxHeightDp by remember {
        mutableStateOf(Offset(0f, 0f))
    }

    val colorSuccess = remember { Animatable(defaultColor) }

    if (isSuccess) {
        LaunchedEffect(key1 = Unit) {
            colorSuccess.animateTo(defaultColor, animationSpec = tween((index + 1) * 50))
            colorSuccess.animateTo(successColor, animationSpec = tween(50))
        }
    }

    val offset by animateOffsetAsState(
        targetValue = if (text.length - 1 >= index) Offset(0f, 0f)
        else boxHeightDp,
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        text[index].toString() == "_" -> " "
        else -> text[index].toString()
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                (2).dp,
                when {
                    isSuccess -> colorSuccess.value
                    isFocused && !color.isRunning -> MaterialTheme.colorScheme.primary
                    else -> color.value
                }, RoundedCornerShape(8.dp)
            )
            .clickable(
                indication = null,
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onClick.invoke(index)
                }
            )
            .then(if (isLoading) Modifier.flickerAnimation() else Modifier)
        , contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .width(32.dp)
                .padding(vertical = 8.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    boxHeightDp =
                        Offset(
                            0f,
                            layoutCoordinates.size.height.toFloat()
                        )
                    boxHeightDp = Offset(boxHeightDp.x, boxHeightDp.y / 5)
                }
                .offset(y = offset.y.dp),
            text = char,
            fontWeight = FontWeight.Bold,
            style = TextStyle(fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }

}


private fun formatInput(str1: String, str2: String): String {
    val commonPrefixLength = str1.zip(str2).takeWhile { (c1, c2) -> c1 == c2 }.count()
    return str1.substring(0, commonPrefixLength) + "_" + str1.substring(commonPrefixLength + 1)
}


fun Modifier.flickerAnimation(
    isLoading: Boolean = true,
    widthOfShadowBrush: Int = 600,
    angleOfAxisY: Float = 0f,
    durationMillis: Int = 1600,
): Modifier {
    return if (isLoading) {
        composed {
            val shimmerColors = getColours()
            val transition = rememberInfiniteTransition(label = "")

            val translateAnimation = transition.animateFloat(
                initialValue = 0f,
                targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durationMillis,
                        easing = LinearEasing,
                    ),
                ),
                label = "flicker animation",
            )

            this.background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                    end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                ),
            )
        }
    } else {
        this
    }
}


private fun getColours(): List<Color> {
    val color = Color.Gray
    return listOf(
        color.copy(alpha = 0.1f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.4f),
        color.copy(alpha = 0.4f),
        color.copy(alpha = 0.2f),
        color.copy(alpha = 0.1f),
    )
}