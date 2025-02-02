package com.temperature.temperatur_sensor_sdk.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.temperature.temperatur_sensor_sdk.R

@Composable
fun DrawerContent(
    onHomeClick: () -> Unit,
    onPieClick: () -> Unit,
    onLineClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width((LocalConfiguration.current.screenWidthDp * 0.75).dp)
    ) {
        Text(
            "Menu",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider()
        StyledNavigationDrawerItem(
            label = "Home",
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        StyledNavigationDrawerItem(
            label = "PM History",
            selected = false,
            onClick = onPieClick,
            icon = {Icon(painter = painterResource(id = R.drawable.ic_stacked_line_chart), contentDescription = "LinePmHistory Chart") },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        StyledNavigationDrawerItem(
            label = "Temp amd Hum History",
            selected = false,
            onClick = onLineClick,
            icon = {Icon(painter = painterResource(id = R.drawable.ic_stacked_line_chart), contentDescription = "Line Chart") },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        StyledNavigationDrawerItem(
            label = "Setting",
            selected = false,
            onClick = onSettingsClick,
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}
