package com.ntg.core.designsystem.components

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.core.mybudget.common.detectCardType
import com.ntg.core.mybudget.common.mask

@Composable
fun BankCard(
  modifier: Modifier = Modifier,
  card: ImageVector? = null,
  cardNumber: String,
  name: String,
  amount: String,
  fullView: Boolean = false,
  onClick: () -> Unit = {},
) {

  var imageModifier by remember {
    mutableStateOf(Modifier.fillMaxWidth())
  }

  var height by remember {
    mutableStateOf(0.dp)
  }

  val localDensity = LocalDensity.current


  val animatedHeight = if (height != 0.dp) {
    animateIntAsState(
      targetValue = if (!fullView) height.value.toInt() else 150,
      label = "alpha",
      animationSpec = tween(
        500,
      ),
    )
  } else {
    animateIntAsState(
      targetValue = if (!fullView) 500 else 150,
      label = "alpha",
      animationSpec = tween(
        500,
      ),
    )
  }


  val animatedAlpha by animateFloatAsState(
    targetValue = if (animatedHeight.value == height.value.toInt()) 1f else 0f,
    label = "alpha",
    animationSpec = tween(
      300,
    ),
  )


  Log.d("animatedHeight", animatedHeight.value.toString())

  Box(
      modifier
          .clickable(
              indication = null,
              onClick = onClick,
              interactionSource = remember { MutableInteractionSource() },
          )
          .height(animatedHeight.value.dp)
          .onGloballyPositioned { layoutCoordinates ->

          },
  ) {

    Image(
      modifier = imageModifier
          .clip(RoundedCornerShape(16.dp))
          .onGloballyPositioned { layoutCoordinates ->
              if (height == 0.dp) {
                  height = with(localDensity) { layoutCoordinates.size.height.toDp() }
              }

          },
      imageVector = card ?: ImageVector.vectorResource(BudgetIcons.bankCard), contentDescription = null, contentScale = ContentScale.Crop,
    )

    Image(
      modifier = Modifier
          .padding(start = 24.dp, top = (height / 3))
          .alpha(animatedAlpha)
          .width(48.dp)
          .height(24.dp),
      imageVector = ImageVector.vectorResource(id = BudgetIcons.chip),
      contentDescription = null,
    )

    CardInfo(
      modifier = Modifier.align(Alignment.BottomCenter),
      cardNumber = cardNumber,
      amount = amount,
      name = name,
      fullView = fullView,
    )

    if (detectCardType(cardNumber).isNotEmpty() && !fullView) {
      Icon(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(24.dp)
            .alpha(animatedAlpha),
        painter = painterResource(id = BudgetIcons.BankLogo.icon(detectCardType(cardNumber))),
        contentDescription = null,
      )
    }

//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .height(172.dp)
//            .background(Color.Black))
  }
}

@Composable
private fun CardInfo(
  modifier: Modifier,
  cardNumber: String,
  name: String,
  amount: String,
  fullView: Boolean,
) {

  val animatedAlpha by animateFloatAsState(
    targetValue = if (!fullView) 0f else 1f,
    label = "alpha",
    animationSpec = tween(
      500,
    ),
  )




  Column(
    modifier = modifier
      .padding(bottom = 32.dp),
  ) {

    Text(
      modifier = Modifier
          .padding(top = 32.dp, bottom = 16.dp)
          .alpha(animatedAlpha),
      text = amount,
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      color = Color.White
    )

    Text(
      text = cardNumber.mask("####  ####  ####  ####  ####  ####"),
      style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
      maxLines = 1,
    )

    Row(
      modifier = Modifier
          .padding(top = 8.dp)
          .alpha(1 - animatedAlpha),
    ) {
      Text(
        text = name,
        style = MaterialTheme.typography.labelMedium.copy(color = Color.White))
    }
  }

//        if (!fullView){
//
//        }else{
//            Spacer(modifier = Modifier.padding(vertical = 8.dp))
//        }


}
