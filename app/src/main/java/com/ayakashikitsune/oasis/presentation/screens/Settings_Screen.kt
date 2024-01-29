package com.ayakashikitsune.oasis.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ayakashikitsune.oasis.presentation.Screen_paths

@Composable
fun Settings_Screen(
    navController: NavController
) {
    val items = listOf(
        "Error Logs",
        "My AI models"
    )
    val action = listOf(
        {
            navController.navigate(Screen_paths.Error_screen.address_id) {
                this.launchSingleTop = true
            }
        },
        {}
    )
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            items.forEachIndexed { index, text ->
                SettingsItem(
                    text = text,
                    onClick = { action[index]() },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
