package com.ntg.core.designsystem.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.ntg.core.mybudget.common.withSuffix
import com.ntg.mybudget.core.designsystem.R
import kotlin.math.PI
import kotlin.math.atan2

@Preview
@Composable
fun ExpenseDonutChartPreview() {

    val data = listOf(
        PieChartInput(color = MaterialTheme.colorScheme.tertiary, value = 18, Title = "Java", brushPattern = R.drawable.default_pattern),
        PieChartInput(color = MaterialTheme.colorScheme.primary, value = 12, Title = "Rust", brushPattern = R.drawable.default_pattern),
        PieChartInput(color = MaterialTheme.colorScheme.secondary, value = 38, Title = "Kotlin", brushPattern = R.drawable.default_pattern),
    )
    ExpenseDonutChart(data, disableClick = false)
}

@Composable
fun ExpenseDonutChart(
    outcomeList: List<PieChartInput>,
    disableClick: Boolean,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .height(215.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.OutcomeWithCategory),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp)
        )
        Row {
            // chart
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .width(120.dp)
            ) {
                PieChart(
                input = outcomeList,
                disableClick = disableClick,
                modifier = Modifier
                    .padding(top = 16.dp))
            }

            Column(
                modifier = Modifier
                    .padding(top = 24.dp, start = 24.dp)
                    .weight(1f)
            ) {
                outcomeList.forEach {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(it.color)
                                .padding(horizontal = 4.dp)
                        )

                        Text(
                            text = it.Title,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                        )

                        Text(
                            text = it.value.toLong().withSuffix(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius: Float = 240f,
    innerRadius: Float = 120f,
    transparentWidth: Float = 60f,
    input: List<PieChartInput>,
    disableClick: Boolean
) {

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var inputList by remember {
        mutableStateOf(input)
    }
    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    val mContext = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = { offset ->
                            val tapAngleInDegrees = (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)
                            val centerClicked = if (tapAngleInDegrees < 90) {
                                offset.x < circleCenter.x + innerRadius && offset.y < circleCenter.y + innerRadius
                            } else if (tapAngleInDegrees < 180) {
                                offset.x > circleCenter.x - innerRadius && offset.y < circleCenter.y + innerRadius
                            } else if (tapAngleInDegrees < 270) {
                                offset.x > circleCenter.x - innerRadius && offset.y > circleCenter.y - innerRadius
                            } else {
                                offset.x < circleCenter.x + innerRadius && offset.y > circleCenter.y - innerRadius
                            }

                            if (!disableClick) {
                                if (centerClicked) {
                                    inputList = inputList.map {
                                        it.copy(isTapped = !isCenterTapped)
                                    }
                                    isCenterTapped = !isCenterTapped
                                } else {
                                    val anglePerValue = 360f / input.sumOf {
                                        it.value
                                    }
                                    var currAngle = 0f
                                    inputList.forEach { pieChartInput ->

                                        currAngle += pieChartInput.value * anglePerValue
                                        if (tapAngleInDegrees < currAngle) {
                                            val description = pieChartInput.Title
                                            inputList = inputList.map {
                                                if (description == it.Title) {
                                                    it.copy(isTapped = !it.isTapped)
                                                } else {
                                                    it.copy(isTapped = false)
                                                }
                                            }
                                            return@detectTapGestures
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f / totalValue
            var currentStartAngle = 0f

            inputList.forEach { pieChartInput ->
                val scale = if (pieChartInput.isTapped && !disableClick) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
                scale(scale){
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius*2f
                        ),
                        topLeft = Offset(
                            (width-radius*2f)/2f,
                            (height - radius*2f)/2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
                var rotateAngle = currentStartAngle - angleToDraw / 2f - 90f
                var factor = 1f
                if (rotateAngle > 90f) {
                    rotateAngle = (rotateAngle + 180).mod(360f)
                    factor = -0.92f
                }

                // text middle of part
                val percentage = (pieChartInput.value/totalValue.toFloat()*100).toInt()
                drawContext.canvas.nativeCanvas.apply {
                    if(percentage>3){
                        rotate(rotateAngle){
                            drawText(
                                "$percentage %",
                                circleCenter.x,
                                circleCenter.y+(radius-(radius-innerRadius)/2f)*factor,
                                Paint().apply {
                                    textSize = 13.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = Color.White.toArgb()
                                }
                            )
                        }
                    }
                }
                if(pieChartInput.isTapped && !disableClick){
                    val tabRotation = currentStartAngle - angleToDraw - 90f
                    rotate(tabRotation) {
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f, radius * 1.2f),
                            color = Color.Gray,
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    rotate(tabRotation + angleToDraw) {
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f, radius * 1.2f),
                            color = Color.Gray,
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    rotate(rotateAngle) {
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "${pieChartInput.Title}: ${pieChartInput.value}",
                                circleCenter.x,
                                circleCenter.y + radius * 1.3f * factor,
                                Paint().apply {
                                    textSize = 22.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = Color.White.toArgb()
                                    isFakeBoldText = true
                                }
                            )
                        }
                    }
                }
            }

            inputList.forEach { pieChartInput ->
                val scale = if (pieChartInput.isTapped && !disableClick) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
//                val brushPattern = ContextCompat.getDrawable(mContext, pieChartInput.brushPattern)?.toBitmap(700, 2000)
                val brush = if (pieChartInput.brushPattern != null) {
                    val brushPattern = ContextCompat.getDrawable(mContext, pieChartInput.brushPattern)?.toBitmap(700, 2000)
                    if (brushPattern != null) {
                        ShaderBrush(ImageShader(brushPattern.asImageBitmap()))
                    } else {
                        SolidColor(Color.Transparent) // Fallback color
                    }
                } else {
                    SolidColor(Color.Transparent) // Fallback color when brushPattern is null
                }

                scale(scale) {
                    drawArc(
                        brush = brush,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius*2f
                        ),
                        topLeft = Offset(
                            (width-radius*2f)/2f,
                            (height - radius*2f)/2f
                        ),
                        colorFilter = ColorFilter.tint(color = Color.White),
                        alpha = 0.5f
                    )
                    currentStartAngle += angleToDraw
                }
            }

            if (inputList.first().isTapped && !disableClick) {
                rotate(-90f) {
                    drawRoundRect(
                        topLeft = circleCenter,
                        size = Size(12f, radius * 1.2f),
                        color = Color.Gray,
                        cornerRadius = CornerRadius(15f, 15f)
                    )
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = backgroundColor.toArgb()
//                        setShadowLayer(10f,0f,0f, Color.Gray.toArgb())
                    }
                )
            }

            drawCircle(
                color = backgroundColor.copy(0.2f),
                radius = innerRadius + transparentWidth / 2f
            )
        }
    }
}

data class PieChartInput(
    val color: Color,
    val brushPattern: Int?=null,
    val value: Long,
    val Title: String,
    val isTapped: Boolean = false
)