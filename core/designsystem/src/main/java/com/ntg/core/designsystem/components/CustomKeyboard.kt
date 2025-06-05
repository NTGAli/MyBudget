package com.ntg.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.core.designsystem.theme.BudgetIcons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorBottomSheet(
    initialValue: String = "",
    onResult: (String) -> Unit,
    onShowSnackbar: suspend (Int, String?, Int?) -> Boolean
) {
    var display by remember { mutableStateOf(if (initialValue.isNotEmpty()) initialValue else "0") }
    var operand1 by remember { mutableStateOf<Double?>(null) }
    var operand2 by remember { mutableStateOf<Double?>(null) }
    var operator by remember { mutableStateOf<String?>(null) }
    var waitingForOperand by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var lastOperation by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    fun clear() {
        display = "0"
        operand1 = null
        operand2 = null
        operator = null
        waitingForOperand = false
        isError = false
        lastOperation = ""
    }

    fun inputDigit(digit: String) {
        if (isError) {
            clear()
            return
        }

        if (waitingForOperand) {
            display = digit
            waitingForOperand = false
        } else {
            display = if (display == "0") digit else display + digit
        }
    }

    fun inputDecimal() {
        if (isError) {
            clear()
            return
        }

        if (waitingForOperand) {
            display = "0."
            waitingForOperand = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun calculate(first: Double, second: Double, op: String): Double {
        return when (op) {
            "+" -> first + second
            "-" -> first - second
            "×" -> first * second
            "÷" -> {
                if (second == 0.0) Double.NaN else first / second
            }
            else -> second
        }
    }

    fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.8f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun performOperation(nextOperator: String) {
        if (isError) return

        val inputValue = display.toDoubleOrNull()
        if (inputValue == null) {
            isError = true
            display = "Error"
            return
        }

        if (operand1 == null) {
            operand1 = inputValue
        } else if (operator != null) {
            operand2 = inputValue
            val result = calculate(operand1!!, operand2!!, operator!!)

            if (result.isNaN() || result.isInfinite()) {
                isError = true
                display = "Error"
                scope.launch {
                    onShowSnackbar(0, "Calculation error", null)
                }
                return
            }

            display = formatResult(result)
            operand1 = result
        }

        waitingForOperand = true
        operator = nextOperator
        lastOperation = "$operand1 $operator"
    }

    fun performEquals() {
        if (isError) return

        val inputValue = display.toDoubleOrNull()
        if (inputValue == null || operand1 == null || operator == null) return

        operand2 = inputValue
        val result = calculate(operand1!!, operand2!!, operator!!)

        if (result.isNaN() || result.isInfinite()) {
            isError = true
            display = "Error"
            scope.launch {
                onShowSnackbar(0, "Calculation error", null)
            }
            return
        }

        display = formatResult(result)
        lastOperation = "$operand1 $operator $operand2 ="
        operand1 = result
        operand2 = null
        operator = null
        waitingForOperand = true

        // Auto submit the result
        onResult(result.toLong().toString())
    }

    fun backspace() {
        if (isError) {
            clear()
            return
        }

        if (display.length > 1) {
            display = display.dropLast(1)
        } else {
            display = "0"
        }
    }

    fun onConfirm() {
        if (isError) {
            scope.launch {
                onShowSnackbar(0, "Calculation error", null)
            }
            return
        }

        val result = display.toDoubleOrNull()
        if (result == null || result <= 0) {
            scope.launch {
                onShowSnackbar(0, "Amount must be positive", null)
            }
            return
        }

        onResult(result.toLong().toString())
    }

    // Force LTR layout for the entire calculator
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Display with forced LTR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Last operation display
                if (lastOperation.isNotEmpty()) {
                    Text(
                        text = lastOperation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.End,
                            textDirection = TextDirection.Ltr
                        )
                    )
                }

                // Main display - Force LTR direction
                Text(
                    text = formatInput(display),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        textDirection = TextDirection.Ltr,
                        textAlign = TextAlign.End,
                        color = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
            }

            // Enhanced keyboard
            EnhancedCustomKeyboard(
                onDigitPressed = { inputDigit(it) },
                onOperatorPressed = { performOperation(it) },
                onEqualsPressed = { performEquals() },
                onDecimalPressed = { inputDecimal() },
                onClearPressed = { clear() },
                onBackspacePressed = { backspace() },
                onConfirm = { onConfirm() }
            )
        }
    }
}

@Composable
fun EnhancedCustomKeyboard(
    onDigitPressed: (String) -> Unit,
    onOperatorPressed: (String) -> Unit,
    onEqualsPressed: () -> Unit,
    onDecimalPressed: () -> Unit,
    onClearPressed: () -> Unit,
    onBackspacePressed: () -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // Left column - digits and basic operations
            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1: Clear, backspace, division
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalculatorKey(
                        label = "C",
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.error
                    ) { onClearPressed() }

                    CalculatorKey(
                        icon = painterResource(id = BudgetIcons.backspace),
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                        labelColor = MaterialTheme.colorScheme.error
                    ) { onBackspacePressed() }

                    CalculatorKey(
                        label = "÷",
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) { onOperatorPressed("÷") }
                }

                // Number rows
                val numberGrid = listOf(
                    listOf("7", "8", "9"),
                    listOf("4", "5", "6"),
                    listOf("1", "2", "3")
                )

                numberGrid.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { digit ->
                            CalculatorKey(label = digit) { onDigitPressed(digit) }
                        }
                    }
                }

                // Bottom row: 0 and decimal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalculatorKey(
                        modifier = Modifier.weight(2f),
                        label = "0"
                    ) { onDigitPressed("0") }

                    CalculatorKey(label = ".") { onDecimalPressed() }
                }
            }

            // Right column - operators and equals
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val operators = listOf("×", "-", "+")

                operators.forEach { operator ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CalculatorKey(
                            label = operator,
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) { onOperatorPressed(operator) }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CalculatorKey(
                        modifier = Modifier.fillMaxHeight(),
                        label = "=",
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        labelColor = MaterialTheme.colorScheme.onPrimary
                    ) { onEqualsPressed() }
                }
            }
        }
    }
}

@Composable
fun RowScope.CalculatorKey(
    modifier: Modifier = Modifier,
    label: String = "",
    icon: Painter? = null,
    iconSize: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(if (modifier == Modifier) Modifier.weight(1f) else Modifier)
            .padding(horizontal = 2.dp)
            .aspectRatio(if (label == "0") 2f else if (label == "=") .5f else 1f)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        val released = tryAwaitRelease()
                        isPressed = false
                        if (released) { // Only execute onClick if the gesture was completed successfully
                            onClick()
                        }
                    }
                )
            }
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = labelColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    textDirection = TextDirection.Ltr
                )
            )
        } else if (icon != null) {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = icon,
                contentDescription = "calculator key",
                tint = labelColor
            )
        }
    }
}

// Keep your original CustomKeyboard for backward compatibility
@Composable
fun CustomKeyboard(
    onKeyPressed: (String) -> Unit,
    onConfirm: () -> Unit,
    onBackspace: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Number Row 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                listOf("7", "8", "9", "÷").forEach { key ->
                    KeyboardKey(label = key) { onKeyPressed(key) }
                }

                KeyboardKey(
                    modifier = Modifier
                        .fillMaxHeight(),
                    icon = painterResource(id = BudgetIcons.backspace),
                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.error
                ) { onBackspace() }

            }

            Row(modifier = Modifier.height(IntrinsicSize.Max)) {

                Column(
                    modifier = Modifier.weight(4f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("4", "5", "6", "×").forEach { key ->
                            KeyboardKey(label = key) { onKeyPressed(key) }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("1", "2", "3", "-").forEach { key ->
                            KeyboardKey(label = key) { onKeyPressed(key) }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(".", "0", "+").forEach { key ->
                            KeyboardKey(label = key) { onKeyPressed(key) }
                        }
                    }
                }

                KeyboardKey(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    label = "OK",
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                ) { onConfirm() }
            }
        }
    }
}

@Composable
fun RowScope.KeyboardKey(
    modifier: Modifier = Modifier,
    label: String = "",
    icon: Painter? = null,
    iconSize: Dp = 24.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    labelColor: Color = MaterialTheme.colorScheme.outlineVariant,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animate the scale value when pressed/released
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // Scale down slightly when pressed
        animationSpec = tween(durationMillis = 100), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .weight(1f)
            .padding(horizontal = 2.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        val released = tryAwaitRelease()
                        isPressed = false
                        if (released) { // Only execute onClick if the gesture was completed successfully
                            onClick()
                        }
                    }
                )
            }
    ) {
        if (label.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    textDirection = TextDirection.Ltr
                )
            )
        } else if (icon != null) {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = icon,
                contentDescription = "key icon",
                tint = labelColor
            )
        }
    }
}

// Additional utility functions
fun formatInput(value: String): String {
    return try {
        val number = value.toLongOrNull() ?: return value
        String.format("%,d", number)
    } catch (e: Exception) {
        value
    }
}

fun calculateExpression(expression: String): Double? {
    return try {
        // Simple expression evaluator for basic operations
        val sanitized = expression.replace(",", "")

        // Handle basic operations
        when {
            sanitized.contains("+") -> {
                val parts = sanitized.split("+")
                if (parts.size == 2) {
                    val first = parts[0].toDoubleOrNull() ?: return null
                    val second = parts[1].toDoubleOrNull() ?: return null
                    first + second
                } else null
            }
            sanitized.contains("-") && sanitized.lastIndexOf("-") > 0 -> {
                val index = sanitized.lastIndexOf("-")
                val first = sanitized.substring(0, index).toDoubleOrNull() ?: return null
                val second = sanitized.substring(index + 1).toDoubleOrNull() ?: return null
                first - second
            }
            sanitized.contains("×") -> {
                val parts = sanitized.split("×")
                if (parts.size == 2) {
                    val first = parts[0].toDoubleOrNull() ?: return null
                    val second = parts[1].toDoubleOrNull() ?: return null
                    first * second
                } else null
            }
            sanitized.contains("÷") -> {
                val parts = sanitized.split("÷")
                if (parts.size == 2) {
                    val first = parts[0].toDoubleOrNull() ?: return null
                    val second = parts[1].toDoubleOrNull() ?: return null
                    if (second == 0.0) null else first / second
                } else null
            }
            else -> sanitized.toDoubleOrNull()
        }
    } catch (e: Exception) {
        null
    }
}

fun isOperator(char: Char): Boolean {
    return char in listOf('+', '-', '×', '÷')
}