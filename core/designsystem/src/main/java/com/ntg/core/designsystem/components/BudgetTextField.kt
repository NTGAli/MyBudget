package com.ntg.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.core.designsystem.util.sanitizeInput
import com.ntg.core.mybudget.common.getCountryName
import com.ntg.core.mybudget.common.getCountryPattern
import com.ntg.core.mybudget.common.number.numToText
import com.ntg.mybudget.core.designsystem.R
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTextField(
    modifier: Modifier = Modifier,
    text: MutableState<String> = remember { mutableStateOf("") },
    setError: MutableState<Boolean> = remember { mutableStateOf(false) },
    supportText: String = "",
    length: Int? = null,
    label: String? = null,
    fixLeadingText: String? = null,
    fixTrailingText: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    leadingIconOnClick: (String) -> Unit = {},
    trailingIcon: Painter? = null,
    trailingIconOnClick: (String) -> Unit = {},
    searchMode: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
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
            if (length != null) {
                if (length < it.length) return@OutlinedTextField
            }
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
        textStyle = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.onSurfaceVariant)
            .copy(textDirection = if (keyboardType == KeyboardType.Number || keyboardType == KeyboardType.Phone || keyboardType == KeyboardType.NumberPassword) TextDirection.Ltr else TextDirection.Rtl),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        trailingIcon =
        if (trailingIcon != null || isPassword || fixTrailingText != null) {
            {

                if (fixTrailingText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerticalDivider(
                            Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .fillMaxHeight(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = fixTrailingText,
                            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.outline)
                        )

                    }
                } else if (trailingIcon != null) {
                    IconButton(onClick = {
                        trailingIconOnClick.invoke(text.value)
                    }) {
                        Icon(
                            painter = trailingIcon,
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
        singleLine = searchMode || singleLine,
        leadingIcon = if (leadingIcon != null || fixLeadingText != null) {
            {
                if (fixLeadingText != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = fixLeadingText,
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
        maxLines = maxLines,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        colors = OutlinedTextFieldDefaults.colors()
            .copy(unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHighest),
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
    val phoneFieldFocus = remember { FocusRequester() }


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

    isRunning(buttonRotate.value == 0f)

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
                            .focusRequester(phoneFieldFocus)
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

    LaunchedEffect(key1 = Unit) {
        phoneFieldFocus.requestFocus()
    }

}


@Composable
fun CurrencyTextField(
    onChange: ((String) -> Unit),
    modifier: Modifier = Modifier,
    locale: Locale = Locale.getDefault(),
    initialText: String = "",
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number),
    maxLines: Int = 1,
    maxNoOfDecimal: Int = 2,
    currencySymbol: String,
    currencyName: String,
    divider: String = ",",
    limit: Int = Int.MAX_VALUE,
    label: String? = null,
    errorColor: Color = LocalTextStyle.current.color,
    errorText: String? = null,
    fixTrailingText: String? = null,
    fixLeadingText: String? = null,
    readOnly: Boolean = false,
    onClick: () -> Unit = {}
) {
    var textFieldState by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialText
            )
        )
    }

    val decimalFormatter: DecimalFormat =
        (NumberFormat.getNumberInstance(locale) as DecimalFormat)
            .apply {
                isDecimalSeparatorAlwaysShown = true
            }

    val decimalFormatSymbols: DecimalFormatSymbols =
        decimalFormatter.decimalFormatSymbols

    val isError by remember(textFieldState.text) {
        mutableStateOf(
            isLimitExceeded(
                limit,
                textFieldState.text,
                currencySymbol,
                decimalFormatter
            )
        )
    }

    var oldText = ""

    Column(
        modifier = Modifier.clickable(enabled = readOnly, indication = null, interactionSource = null, onClick = {
            onClick()
        }),
        horizontalAlignment = Alignment.End
    ) {
        OutlinedTextField(
            value = textFieldState,
            label = if (!label.isNullOrEmpty()) {
                {
                    Text(
                        text = label, maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else null,
            modifier = modifier
                .height(80.dp)
                .wrapContentSize(align = Alignment.CenterStart)
                .fillMaxWidth()
                .clickable(
                    enabled = true,
                    onClick = { onClick.invoke() },
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                ),
            onValueChange = {
                if (it.text.length > 19) return@OutlinedTextField
                textFieldState = formatUserInput(
                    oldText,
                    sanitizeInput(
                        currencySymbol,
                        decimalFormatSymbols,
                        it
                    ),
                    decimalFormatSymbols,
                    maxNoOfDecimal,
                    currencySymbol,
                    decimalFormatter,
                    divider
                )
                oldText = textFieldState.text
                onChange(oldText)
            },
            keyboardOptions = keyboardOptions,
            maxLines = maxLines,
            singleLine = true,
            readOnly = readOnly,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Left),
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors()
                .copy(unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHighest),
            leadingIcon = if (fixLeadingText != null) {
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerticalDivider(
                            Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .fillMaxHeight(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = fixLeadingText,
                            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.outline)
                        )

                    }
                }
            } else null,
            trailingIcon = if (fixTrailingText != null) {
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerticalDivider(
                            Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .fillMaxHeight(),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = fixTrailingText,
                            style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.outline)
                        )

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
        )

        AnimatedVisibility(visible = isError && errorText?.isNotEmpty() == true) {
            Text(
                text = errorText ?: "",
                modifier = Modifier
                    .padding(end = 10.dp),
                fontSize = 16.sp,
                color = if (isError) errorColor else LocalTextStyle.current.color
            )
        }

        AnimatedVisibility(visible = textFieldState.text.isNotEmpty()) {
            Text(
                text = "${numToText(textFieldState.text)} $currencyName",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
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

private fun isLimitExceeded(
    limit: Int,
    currentAmount: String,
    currencySymbol: String,
    decimalFormatter: DecimalFormat
): Boolean {
    val cleanedInput = currentAmount.replace(currencySymbol, "")
    if (cleanedInput.isEmpty()) {
        return false
    }
    return (decimalFormatter.parse(cleanedInput)?.toInt() ?: 0) > limit
}

private fun formatUserInput(
    oldText: String,
    textFieldValue: TextFieldValue,
    decimalFormatSymbols: DecimalFormatSymbols,
    maxNoOfDecimal: Int,
    currencySymbol: String,
    decimalFormatter: DecimalFormat,
    divider: String
): TextFieldValue {
    if (oldText == textFieldValue.text)
        return TextFieldValue(
            text = oldText,
            selection = TextRange(oldText.length)
        )

    if (textFieldValue.text.length < currencySymbol.length) {
        return TextFieldValue(
            text = currencySymbol,
            selection = TextRange(currencySymbol.length)
        )
    }

    if (textFieldValue.text == currencySymbol) {
        return TextFieldValue(
            text = currencySymbol,
            selection = TextRange(currencySymbol.length)
        )
    }

    var userInput = textFieldValue.text
    var finalSelection: Int = 0

    if (userInput.last().toString() == "." &&
        decimalFormatSymbols.decimalSeparator.toString() != userInput.last().toString()
    ) {
        userInput = userInput.dropLast(1)
        userInput.plus(decimalFormatSymbols.decimalSeparator.toString())
    }

    if (checkDecimalSizeExceeded(
            userInput,
            decimalFormatSymbols,
            maxNoOfDecimal
        ).not()
    ) {

        userInput = userInput.replace(currencySymbol, "")
        val startLength = textFieldValue.text.length

        try {
            val parsedNumber = decimalFormatter.parse(userInput)
            decimalFormatter.applyPattern(
                setDecimalFormatterSensitivity(
                    userInput, decimalFormatSymbols, maxNoOfDecimal, divider
                )
            )

            val startPoint = textFieldValue.selection.start
            userInput = "$currencySymbol${decimalFormatter.format(parsedNumber)}"
            val finalLength = userInput.length
            val selection = startPoint + (finalLength - startLength)

            finalSelection = if (selection > 0 && selection <= userInput.length) {
                selection
            } else {
                userInput.length - 1
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    } else {
        finalSelection = userInput.length - 1
        userInput = userInput.substring(0, userInput.length - 1)
    }

    return TextFieldValue(
        text = userInput,
        selection = TextRange(finalSelection)
    )
}

private fun setDecimalFormatterSensitivity(
    userInput: String,
    decimalFormatSymbols: DecimalFormatSymbols,
    maxNoOfDecimal: Int,
    divider: String = ","
): String {

    val decimalSeparatorIndex = userInput.indexOf(decimalFormatSymbols.decimalSeparator)
    if (decimalSeparatorIndex == -1)
        return "#$divider##0"

    val noOfCharactersAfterDecimalPoint =
        userInput.length - decimalSeparatorIndex - 1

    val zeros = "0".repeat(
        min(
            noOfCharactersAfterDecimalPoint,
            maxNoOfDecimal
        )
    )
    return "#$divider##0.$zeros"

}

private fun checkDecimalSizeExceeded(
    input: String,
    decimalFormatSymbols: DecimalFormatSymbols,
    maxNoOfDecimal: Int
): Boolean {
    return (input.split(decimalFormatSymbols.decimalSeparator)
        .getOrNull(1)?.length ?: Int.MIN_VALUE) > maxNoOfDecimal

}


