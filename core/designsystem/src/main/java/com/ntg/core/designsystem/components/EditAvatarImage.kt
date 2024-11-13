package com.ntg.core.designsystem.components

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.designsystem.util.CompressImage

@Composable
fun EditAvatarImage(
    imageUrl1: String,
    onResult: (Bitmap, String) -> Unit
) {
    // Initialize the URI with the server image URL
    var imageUri by remember(imageUrl1) { mutableStateOf<Uri?>(Uri.parse(imageUrl1)) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri == null || uri.toString().isEmpty()) return@rememberLauncherForActivityResult
        imageUri = uri

        val compressImage = CompressImage(context, 300.0f)
        val bitmap = compressImage.compressImage(uri.toString())

        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.path)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/png"

        if (bitmap != null)
            onResult.invoke(bitmap, mimeType)
    }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .size(67.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            error = painterResource(BudgetIcons.UserCircle),
            contentDescription = "Profile image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(64.dp)
        )

        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomEnd),
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(id = BudgetIcons.edit),
                tint = Color.White,
                modifier = Modifier.size(12.dp),
                contentDescription = "edit profile image"
            )
        }
    }
}