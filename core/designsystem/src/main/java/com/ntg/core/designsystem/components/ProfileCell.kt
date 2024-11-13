package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.ntg.core.designsystem.theme.BudgetIcons

@Preview
@Composable
fun ProfileCellPreview() {
    ProfileCell(
        "UserMail@mail.com",
        "نیلا ایمانی",
        "https://imgcdn.stablediffusionweb.com/2024/2/28/32f87d26-3cd0-4733-8f86-38a81c8e0f3a.jpg",
        false,
    ) {}
}

@Composable
fun ProfileCell(
    email: String,
    name: String,
    imageUrl: String,
    isLoading: Boolean,
    onEditClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable { onEditClicked.invoke() }
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
//                placeholder = painterResource(BudgetIcons.UserCircle),
                error = painterResource(BudgetIcons.UserCircle),
                contentDescription = "Profile image",
                contentScale = ContentScale.Crop,
                modifier = flickerModifier(shape = CircleShape, width = 64.dp, height = 64.dp, isLoading = isLoading, isProfile = true)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = flickerModifier(isLoading, width = 144.dp),
                    text = if (!isLoading) email else "",
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )

                Text(
                    modifier = flickerModifier(isLoading, width = 90.dp, padding = PaddingValues(top = 6.dp)),
                    text = if (!isLoading) name else "",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }

        IconButton(
            onClick = { onEditClicked.invoke() }
        ) {
            Icon(
                painter = painterResource(BudgetIcons.edit),
                contentDescription = null,
            )
        }
    }
}

fun flickerModifier(
    isLoading: Boolean,
    isProfile: Boolean = false,
    width: Dp,
    height: Dp = 24.dp,
    shape: RoundedCornerShape = RoundedCornerShape(6.dp),
    padding: PaddingValues = PaddingValues()
): Modifier {
    return if (isLoading) {
        Modifier
            .padding(padding)
            .width(width)
            .height(height)
            .clip(shape)
            .flickerAnimation()
    } else if (isProfile) {
        Modifier
            .clip(CircleShape)
            .size(64.dp)
    } else {
        Modifier
    }
}