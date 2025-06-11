package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.mybudget.common.getCurrentJalaliDate
import com.ntg.core.mybudget.common.jalaliToTimestamp
import com.ntg.core.mybudget.common.persianDate.PersianDate
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateItem(
    modifier: Modifier = Modifier,
    onChangeTime: (Long) -> Unit
) {

    var openSheet by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    val selectedDateState = remember {
        mutableStateListOf<String>()
    }

    val selectedTimeState = remember {
        mutableStateListOf<String>()
    }

    var daysInMonth by remember {
        mutableIntStateOf(31)
    }

    var currentYear by remember {
        mutableIntStateOf(1400)
    }

    var type by remember {
        mutableStateOf(0)
    }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .background(
                shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surfaceContainer
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                openSheet = true
            }
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedDate,
            style = MaterialTheme.typography.labelMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(4.dp)
                .background(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceDim)
        )

        Text(
            text = selectedTime,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDirection = TextDirection.Ltr
            )
        )

        Icon(
            painter = painterResource(id = BudgetIcons.CalenderTick),
            contentDescription = "calender",
            tint = MaterialTheme.colorScheme.outline
        )
    }

    val months = listOf(
        "فروردین",
        "اردیبهشت",
        "خرداد",
        "تیر",
        "مرداد",
        "شهریور",
        "مهر",
        "آبان",
        "آذر",
        "دی",
        "بهمن",
        "اسفند"
    )



    LaunchedEffect(key1 = sheetState.currentValue) {
        openSheet = sheetState.isVisible
    }

    LaunchedEffect(key1 = Unit) {
        selectedDateState.add(0, getCurrentJalaliDate().first.toString())
        selectedDateState.add(1, months[getCurrentJalaliDate().second - 1])
        selectedDateState.add(2, getCurrentJalaliDate().third.toString())
        selectedDate = "${selectedDateState[2]} ${selectedDateState[1]} ${selectedDateState[0]}"
        currentYear = getCurrentJalaliDate().first

        val currentTime = LocalTime.now()
        selectedTimeState.add(0, currentTime.hour.toString())
        selectedTimeState.add(1, currentTime.minute.toString())
        selectedTime = "${selectedTimeState[0]} : ${selectedTimeState[1]}"

        onChangeTime(
            jalaliToTimestamp(
                year = selectedDateState[0].toInt(),
                month = months.indexOfFirst { it == selectedDateState[1] }+1,
                day = selectedDateState[2].toInt(),
                hour = selectedTimeState[0].toInt(),
                minute = selectedTimeState[1].toInt()
            )
        )
    }

    if (selectedDateState.isNotEmpty()) {
        LaunchedEffect(key1 = selectedDateState[1]) {
            daysInMonth = when (months.indexOfFirst { it == selectedDateState[1] } + 1) {
                in 1..6 -> 31
                in 7..11 -> 30
                12 -> if (PersianDate().isLeap(selectedDateState[0].toInt())) 30 else 29
                else -> throw IllegalArgumentException("Invalid month in Jalali calendar")
            }
        }
    }

    if (openSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { openSheet = false }) {

            Column {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Row(
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {

                        if (type == 0) {
                            // year
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (currentYear - 5..currentYear).toList(),
                                initialItem = selectedDateState[0].toInt(),
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(0)
                                    selectedDateState.add(
                                        0,
                                        (currentYear - 5..currentYear).toList()[i].toString()
                                    )
                                }
                            )

                            //month
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = months,
                                initialItem = selectedDateState[1],
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(1)
                                    selectedDateState.add(1, months[i])
                                }
                            )

                            //day
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..daysInMonth).toList(),
                                initialItem = selectedDateState[2].toInt(),
                                onItemSelected = { i, item ->
                                    selectedDateState.removeAt(2)
                                    selectedDateState.add(2, (1..31).toList()[i].toString())
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..24).toList(),
                                initialItem = selectedTimeState[0].toInt(),
                                onItemSelected = { i, item ->
                                    selectedTimeState.removeAt(0)
                                    selectedTimeState.add(0, item.toString())
                                }
                            )

                            WheelList(
                                modifier = Modifier.weight(1f),
                                items = (1..59).toList(),
                                initialItem = selectedTimeState[1].toInt(),
                                onItemSelected = { i, item ->
                                    selectedTimeState.removeAt(1)
                                    selectedTimeState.add(1, item.toString())
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                BudgetButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .padding(bottom = 24.dp),
                    text = stringResource(id = R.string.submit)
                ) {
                    selectedDate =
                        "${selectedDateState[2]} ${selectedDateState[1]} ${selectedDateState[0]}"
                    selectedTime = "${selectedTimeState[0]} : ${selectedTimeState[1]}"
                    if (type == 1) {
                        onChangeTime(
                            jalaliToTimestamp(
                                year = selectedDateState[0].toInt(),
                                month = months.indexOfFirst { it == selectedDateState[1] }+1,
                                day = selectedDateState[2].toInt(),
                                hour = selectedTimeState[0].toInt(),
                                minute = selectedTimeState[1].toInt()
                            )
                        )
                        scope.launch {
                            type = 0
                            sheetState.hide()
                        }
                        return@BudgetButton
                    }
                    onChangeTime(
                        jalaliToTimestamp(
                            year = selectedDateState[0].toInt(),
                            month = months.indexOfFirst { it == selectedDateState[1] }+1,
                            day = selectedDateState[2].toInt(),
                            hour = selectedTimeState[0].toInt(),
                            minute = selectedTimeState[1].toInt()
                        )
                    )
                    type = 1
                }
            }


        }
    }

}
