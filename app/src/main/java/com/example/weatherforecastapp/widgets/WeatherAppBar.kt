package com.example.weatherforecastapp.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppBar(
    title: String = "Title",
    icon: ImageVector? = null,
    isMainScreen: Boolean = true,
    elevation: Dp = 0.dp,
    navController: NavController,
    onAddActionClicked: () -> Unit = {},
    onButtonClicked: () -> Unit = {}
) {
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
                        modifier = Modifier.
                        clickable {
                            onButtonClicked.invoke()
                        }
                    )
                }
            },
        )
    }
}