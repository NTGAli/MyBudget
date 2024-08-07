package com.ntg.core.designsystem.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.core.mybudget.common.getCountryName
import com.ntg.core.mybudget.common.getCountryPattern
import com.ntg.mybudget.core.designsystem.R

@Composable
fun BudgetTextField(
    modifier: Modifier = Modifier,
    text: MutableState<String> = remember { mutableStateOf("") },
    setError: MutableState<Boolean> = remember { mutableStateOf(false) },
    supportText: String = "",
    label: String? = null,
    fixText: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    leadingIconOnClick: (String) -> Unit = {},
    trailingIcon: ImageVector? = null,
    trailingIconOnClick: (String) -> Unit = {},
    searchMode: Boolean = false,
    singleLine: Boolean = true,
    onClick: () -> Unit = {},
    onChange: (String) -> Unit = {}

) {

    var passwordVisible by rememberSaveable { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .width(2.dp)
            .clickable(
                enabled = true,
                onClick = { onClick.invoke() },
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            )
            .focusRequester(focusRequester),
        value = text.value,
        onValueChange = {
            text.value = it
            onChange.invoke(it)
        },
        label = if (!searchMode) {
            {
                if (!label.isNullOrEmpty()) {
                    Text(
                        text = label, maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        } else null,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.onSurfaceVariant),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        trailingIcon =
        if (trailingIcon != null || isPassword) {
            {
                if (trailingIcon != null) {
                    IconButton(onClick = {
                        trailingIconOnClick.invoke(text.value)
                    }) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = "leading"
                        )
                    }
                } else {
                    // for password
                    val image = if (passwordVisible)
                        Icons.Rounded.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            }

        } else null,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onClick.invoke()
                        }
                    }
                }
            },
        isError = setError.value,
        supportingText = if (supportText.isNotEmpty()) {
            {

//            if (supportText.isNotEmpty()) {
                Text(text = supportText)
//            }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(),
        singleLine = searchMode || singleLine,
        leadingIcon = if (leadingIcon != null || fixText != null) {
            {
                if (fixText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = fixText,
                            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.outline)
                        )
                        VerticalDivider(
                            Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .fillMaxHeight(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    }
                } else {
                    IconButton(onClick = {
                        leadingIconOnClick.invoke(text.value)
                    }) {
                        Icon(
                            imageVector = leadingIcon!!,
                            contentDescription = "leading"
                        )
                    }
                }

            }

        } else null,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    )

    LaunchedEffect(Unit) {
        if (searchMode) {
            focusRequester.requestFocus()
        }
    }

}


@Composable
fun BudgetTextField(
    modifier: Modifier = Modifier,
    code: MutableState<String> = remember { mutableStateOf("") },
    phone: MutableState<String> = remember { mutableStateOf("") },
    wasWrong: Boolean,
    isRunning: (Boolean) -> Unit = {}
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = phone.value)) }
    var textFieldValue = textFieldValueState.copy(text = phone.value)

    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }

    var mask by remember {
        mutableStateOf("")
    }
    val maskNumber = '0'

    val buttonRotate = remember { Animatable(0f) }


    LaunchedEffect(wasWrong) {
        if (!buttonRotate.isRunning && wasWrong) {

            buttonRotate.animateTo(0f, keyframes {
                durationMillis = 250
                0f at 0
                -15f at 150
            })
        }
    }
    mask = getCountryPattern(context, code.value)
    val annotatedString = remember {
        mutableStateOf(
            AnnotatedString("")
        )
    }

    annotatedString.value = buildAnnotatedString {
        if (phone.value.isEmpty()) return@buildAnnotatedString

        var maskIndex = 0
        var textIndex = 0
        while (textIndex < phone.value.length && maskIndex < mask.length) {
            if (mask[maskIndex] != maskNumber) {
                val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                append(mask.substring(maskIndex, nextDigitIndex))
                maskIndex = nextDigitIndex
            }
            append(phone.value[textIndex++])
            maskIndex++
        }

        pushStyle(SpanStyle(color = MaterialTheme.colorScheme.outline))
        append(mask.takeLast(mask.length - length))
        toAnnotatedString()
    }


    val textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.inverseSurface)
    val hintStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.outline)



    if (code.value.length >= 4) {
        LaunchedEffect(key1 = Unit) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Next,
            )

        }

    }


    var isBackspaceClicked by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(key1 = isBackspaceClicked) {
        Log.d("LaunchedEffect", "LaunchedEffect")
        if (phone.value.isEmpty() && isBackspaceClicked || isBackspaceClicked) {
            focusManager.moveFocus(
                focusDirection = FocusDirection.Previous,
            )
            isBackspaceClicked = false
        }
    }




    if (getCountryName(context, code.value) != "---") {
        LaunchedEffect(
            key1 = code.value,
            block = {

                focusManager.moveFocus(
                    focusDirection = FocusDirection.Next,
                )
            })
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = buttonRotate.value.dp),
    ) {

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier
                    .semantics(mergeDescendants = true) {}
                    .padding(top = 8.dp)
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 18.dp)
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically

            ) {

                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = "+",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                )

                BasicTextField(
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = (2).dp),
                    value = code.value,
                    onValueChange = {
                        code.value = it

                    },
                    textStyle = textStyle,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                )


                VerticalDivider(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                )

                Box(
                    modifier = Modifier
                        .weight(10f)
                ) {

                    if (annotatedString.value.text.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text =
                            mask,
                            style = hintStyle.copy(color = MaterialTheme.colorScheme.outline)
                        )
                    } else {
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text =
                            annotatedString.value,
                            style = hintStyle.copy(color = MaterialTheme.colorScheme.outline)
                        )
                    }



                    BasicTextField(

                        value = phone.value,

                        onValueChange = { newTextFieldValueState ->
                            val trimMask = mask.replace(" ", "")
                            if (newTextFieldValueState.length <= trimMask.length ||
                                mask.isEmpty()
                            ) {
                                phone.value = newTextFieldValueState
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = if (mask.isNotEmpty()) PhoneVisualTransformation(
                            mask, '0'
                        ) else VisualTransformation.None,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .onPreviewKeyEvent {
                                if (it.key == Key.Backspace) {
                                    if (phone.value.isEmpty()) {
                                        code.value = code.value.dropLast(1)
                                        isBackspaceClicked = true
                                    }

                                    true
                                } else {
                                    false
                                }
                                false
                            },
                        textStyle = textStyle,
                        maxLines = 1,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),

                        )


                }


            }
        }



        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 4.dp),
            text = stringResource(R.string.phone_number),
            style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
        )

    }


}


class PhoneVisualTransformation(val mask: String, val maskNumber: Char) : VisualTransformation {

    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskNumber) {
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskNumber != other.maskNumber) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {

        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            try {
                if (mask[i++] != numberChar) noneDigitCount++
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}


