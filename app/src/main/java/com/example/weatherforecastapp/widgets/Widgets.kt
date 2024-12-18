package com.example.weatherforecastapp.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.model.WeatherItem
import com.example.weatherforecastapp.utils.formatDate
import com.example.weatherforecastapp.utils.formatDateTime
import com.example.weatherforecastapp.utils.formatDecimals


@Composable
fun WeatherDetailRow(weather: WeatherItem) {
    val imageUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png"

    // Outer Box with shadow effect
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp) // Space around the shadowed card
            .shadow(
                elevation = 12.dp, // Shadow elevation
                shape = CircleShape.copy(
                    topEnd = CornerSize(6.dp),
                    bottomStart = CornerSize(6.dp)
                ), // Preserve custom shape
                clip = false
            )
    ) {
        // Main content card
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = CircleShape.copy(
                topEnd = CornerSize(6.dp),
                bottomStart = CornerSize(6.dp)
            ), // Preserve shape
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp), // Adds spacing within the row
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Date Text
                Text(
                    text = formatDate(weather.dt).split(",")[0],
                    style = MaterialTheme.typography.bodyMedium
                )

                // Weather Icon
                WeatherStateImage(imageUrl = imageUrl)

                // Weather Description inside Ellipse
                Surface(
                    modifier = Modifier.size(
                        width = 90.dp,
                        height = 32.dp
                    ), // Slightly smaller ellipse
                    shape = CircleShape,
                    color = Color(0xFFFFC400)
                ) {
                    Box(
                        contentAlignment = Alignment.Center, // Centers the text inside the ellipse
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = weather.weather[0].description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Max and Min Temperatures
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(formatDecimals(weather.temp.max) + "º")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color.LightGray
                            )
                        ) {
                            append(" / " + formatDecimals(weather.temp.min) + "º")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun SunsetSunRiseRow(weather: WeatherItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.sunrise),
                contentDescription = "sunrise",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = formatDateTime(weather.sunrise),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row {
            Text(
                text = formatDateTime(weather.sunset),
                style = MaterialTheme.typography.bodyMedium
            )
            Image(
                painter = painterResource(id = R.drawable.sunset),
                contentDescription = "sunset",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun HumidityWindPressureRow(
    weather: WeatherItem,
    isImperial: Boolean
) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.humidity),
                contentDescription = "humidity icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.humidity}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row {
            Icon(
                painter = painterResource(id = R.drawable.pressure),
                contentDescription = "pressure icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.pressure} psi",
                style = MaterialTheme.typography.bodyMedium
            )

        }

        Row {
            Icon(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "wind icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${formatDecimals(weather.speed)} " + if (isImperial) "mph" else "m/s",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun WeatherStateImage(imageUrl: String) {
    Image(
        painter = rememberImagePainter(imageUrl),
        contentDescription = "icon image",
        modifier = Modifier.size(80.dp)
    )

}