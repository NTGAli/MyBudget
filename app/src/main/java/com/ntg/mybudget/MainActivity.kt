package com.ntg.mybudget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.components.AmountReport
import com.ntg.core.designsystem.components.BottomNavigation
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.CardReport
import com.ntg.core.designsystem.components.DateDivider
import com.ntg.core.designsystem.components.TransactionItem
import com.ntg.core.designsystem.model.NavigationItem
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.designsystem.theme.MyBudgetTheme
import com.ntg.core.designsystem.theme.SurfaceDark
import com.ntg.core.designsystem.theme.SurfaceLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
//            navigationBarStyle = SystemBarStyle.auto(SurfaceLight.toArgb(), SurfaceDark.toArgb())
        )


        setContent {
            val text = remember { mutableStateOf("") }
            val code = remember { mutableStateOf("") }
            MyBudgetTheme {
                Scaffold(modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize(),
                    bottomBar = {

                        var expand by remember {
                            mutableStateOf(false)
                        }

                        val navs = listOf(
                            NavigationItem(
                                1,
                                "test",
                                painterResource(id = BudgetIcons.Home),
                                painterResource(id = BudgetIcons.Home),
                                isSelected = true,
                            ),
                            NavigationItem(
                                2,
                                "test",
                                painterResource(id = BudgetIcons.Home),
                                painterResource(id = BudgetIcons.Home),
                                isSelected = false,
                            ),
                        )
                        BottomNavigation(modifier = Modifier, items = navs, expand){
                            expand = !expand
                        }

                    }
                    ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {

                        BudgetTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 24.dp),
                            phone = text,
                            code = code
                        )


                        BudgetTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(top = 24.dp),
                            text = text,
                            label = "HELLO",
                            fixText = "تومن"
                        )


                        AmountReport(outcome = "120000", income = "500000")

                        CardReport(
                            modifier = Modifier.padding(24.dp),
                             title = "140000 ت",
                            subTitle = "موجودی همه ی حساب ها",
                            out = "140000",
                            inValue = "140000"
                        )

                        DateDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            date = "امروز 10 مرداد", amount = "10.500", type = "income")

                        TransactionItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 8.dp),
                            amount = "24000",
                            title = "کافه",
                            date = "13:45",
                            type = "income",
                            divider = true
                        )

                        TransactionItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            amount = "24000",
                            title = "کافه",
                            date = "13:45",
                            type = "income",
                            divider = true,
                            attached = true
                        )

                        TransactionItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            amount = "24000",
                            title = "کافه",
                            date = "13:45",
                            type = "Outcome"
                        )
                        


                    }
                }
            }
        }
    }
}