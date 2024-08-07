package com.ntg.mybudget.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.ui.graphics.vector.ImageVector
import com.ntg.mybudget.R

enum class TopLevelDestination(
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val iconTextId: Int,
  val titleTextId: Int,
) {
  HOME(
    selectedIcon = Icons.Rounded.Add,
    unselectedIcon = Icons.Rounded.Add,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
  ),

  TRANSACTION(
    selectedIcon = Icons.Rounded.Add,
    unselectedIcon = Icons.Rounded.Add,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
  ),

  SETUP(
    selectedIcon = Icons.Rounded.Add,
    unselectedIcon = Icons.Rounded.Add,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
  ),
   PROFILE(
    selectedIcon = Icons.Rounded.Add,
    unselectedIcon = Icons.Rounded.Add,
    iconTextId = R.string.app_name,
    titleTextId = R.string.app_name,
  ),
}
