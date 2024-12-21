package com.example.weatherforecastapp.widgets

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecastapp.model.Favorite
import com.example.weatherforecastapp.navigation.WeatherScreens
import com.example.weatherforecastapp.screens.favorites.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppBar(
    title: String = "Title",
    icon: ImageVector? = null,
    isMainScreen: Boolean = true,
    elevation: Dp = 0.dp,
    navController: NavController,
    favoriteViewModel: FavoriteViewModel = hiltViewModel(),
    onAddActionClicked: () -> Unit = {},
    onButtonClicked: () -> Unit = {}
) {

    val showDialog = remember {
        mutableStateOf(false)
    }

    if (showDialog.value) {
        ShowSettingDropDownMenu(showDialog = showDialog, navController = navController)
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.error,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                )
            },
            actions = {
                if (isMainScreen) {
                    IconButton(onClick = {
                        onAddActionClicked.invoke()
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon")
                    }

                    IconButton(onClick = {
                        showDialog.value = true
                    }) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "More icon")
                    }
                } else Box {}
            },
            navigationIcon = {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "navigationIcon",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable {
                            onButtonClicked.invoke()
                        }
                    )
                }
                if (isMainScreen) {
                    val dataList = title.split(",")
                    val isAlreadyFavList = favoriteViewModel.favList.collectAsState().value.filter { item ->
                        item.city == dataList[0]
                    }

                    Icon(
                        imageVector = if (isAlreadyFavList.isEmpty()) Icons.Default.FavoriteBorder else Icons.Filled.Favorite,
                        contentDescription = "Favorite icon",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier
                            .scale(0.9f)
                            .clickable {
                                val favorite = Favorite(
                                    city = dataList[0].trim(),
                                    country = dataList[1].trim()
                                )
                                val isCityExist = isAlreadyFavList.contains(favorite)
                                if (isCityExist){
                                    favoriteViewModel.deleteFavorite(favorite)

                                } else {
                                    favoriteViewModel.insertFavorite(favorite)
                                }
                            }
                    )
                }
            },
        )
    }
}

@Composable
fun ShowSettingDropDownMenu(
    showDialog: MutableState<Boolean>,
    navController: NavController
) {
    val expanded = remember {
        mutableStateOf(true)
    }
    val items = listOf("About", "Favorites", "Settings")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .absolutePadding(top = 45.dp, right = 20.dp)
    ) {
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .width(175.dp)
                .background(Color.White)
        ) {
            items.forEachIndexed { _, text ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = text,
                            fontWeight = FontWeight.W300
                        )
                    },
                    onClick = {
                        expanded.value = false
                        showDialog.value = false
                        navController.navigate(
                            when (text) {
                                "About" -> WeatherScreens.AboutScreen.name
                                "Favorites" -> WeatherScreens.FavoriteScreen.name
                                else -> WeatherScreens.SettingsScreen.name
                            }
                        )
                    },
                    modifier = Modifier,
                    leadingIcon = {
                        Icon(
                            imageVector = when (text) {
                                "About" -> Icons.Default.Info
                                "Favorites" -> Icons.Default.FavoriteBorder
                                else -> Icons.Default.Settings
                            },
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    },
                    trailingIcon = { },
                    enabled = true,
                    colors = MenuDefaults.itemColors(),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                )
            }
        }
    }
}