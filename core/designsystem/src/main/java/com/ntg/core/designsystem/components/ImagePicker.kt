package com.ntg.core.designsystem.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.core.designsystem.theme.BudgetIcons
import com.ntg.designsystem.util.CompressImage
import com.ntg.mybudget.core.designsystem.R
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImagePicker(
  modifier: Modifier = Modifier,
  padding: PaddingValues = PaddingValues(),
  imagePaths:(List<String>) -> Unit = {}
) {

  val context = LocalContext.current

  var imagePath by rememberSaveable {
    mutableStateOf<ArrayList<String>>(arrayListOf())
  }


  val mediaPicker = rememberLauncherForActivityResult(
      contract = ActivityResultContracts.PickVisualMedia(),
      onResult = { uri ->
          if (uri == null || uri.toString().isEmpty()) return@rememberLauncherForActivityResult
          val compressImage = CompressImage(context)
          val bitmap = compressImage.compressImage(uri.toString())
          imagePath = ArrayList(imagePath).apply {
              add(
                  saveImageInFolder(
                      bitmap,
                      context,
                      System.currentTimeMillis().toString(),
                  ).path,
              )
          }
        imagePaths(imagePath)
//            imagePath = uri.path
      },
  )

  if (imagePath.isEmpty()) {
    Column(
        modifier = modifier
            .padding(padding)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                mediaPicker
                    .launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
            }
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(painter = painterResource(id = BudgetIcons.CameraPlus), contentDescription = null)

      Text(
          modifier = Modifier.padding(top = 4.dp),
          text = stringResource(id = R.string.add_image),
          style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.outline),
      )
    }
  } else {

    LazyRow(
        modifier = modifier.padding(top = padding.calculateTopPadding()),
        contentPadding = PaddingValues(horizontal = 32.dp),
    ) {
      items(imagePath) { path ->
        Box {
          Image(
              modifier = Modifier
                  .padding(end = 12.dp)
                  .size(64.dp)
                  .clip(RoundedCornerShape(8.dp)),
              bitmap = loadImageFromFile(filePath = path)!!.asImageBitmap(), contentDescription = null,
              contentScale = ContentScale.Crop,
          )

          Icon(
              modifier = Modifier
                  .size(16.dp)
                  .offset(y = (-6).dp, x = (-6).dp)
                  .align(Alignment.TopEnd)
                  .clip(CircleShape)
                  .background(color = MaterialTheme.colorScheme.errorContainer, shape = CircleShape)
                  .clickable {
                      imagePath = ArrayList(imagePath).apply { remove(path) }
                  },
              painter = painterResource(id = BudgetIcons.Close),
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
          )
        }
      }

      item {
        InsertImage(mediaPicker)
      }
    }


  }


}

@Composable
private fun InsertImage(mediaPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
  Icon(
      modifier = Modifier

          .clip(RoundedCornerShape(8.dp))

          .clickable {
              mediaPicker
                  .launch(
                      PickVisualMediaRequest(
                          ActivityResultContracts.PickVisualMedia.ImageOnly,
                      ),
                  )
          }
          .size(64.dp)
          .border(
              width = 2.dp,
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primary,
          )
          .padding(24.dp),
      painter = painterResource(id = R.drawable.cameraplus_24),
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
  )
}


private fun saveImageInFolder(bitmap: Bitmap?, context: Context, name: String): File {
  val directory = File(
      context.getExternalFilesDir("backups/images"),
      "${name}${System.currentTimeMillis()}.jpeg",
  )
  directory.parentFile?.mkdir()

  val fos: FileOutputStream?
  try {
    fos = FileOutputStream(directory)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//        bitmap?.compressBitmap(fos)
    fos.close()
  } catch (e: java.lang.Exception) {
//        timber("SAVE IMAGE ERR :: ${e.message}")
  }

  return directory
}

@Composable
fun loadImageFromFile(filePath: String): Bitmap? {
  val file = File(filePath)
  if (file.exists()) {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    return bitmap
  }
  return null
}
