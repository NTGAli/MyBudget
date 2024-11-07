package com.ntg.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
        "https://imgcdn.stablediffusionweb.com/2024/2/28/32f87d26-3cd0-4733-8f86-38a81c8e0f3a.jpg"
    ) {}
}

@Composable
fun ProfileCell(
    email: String,
    name: String,
    imageUrl: String,
    onEditClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
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
                placeholder = painterResource(BudgetIcons.UserCircle),
                error = painterResource(BudgetIcons.UserCircle),
                contentDescription = "Profile image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(64.dp)
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = email,
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )

                Text(
                    text = name,
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