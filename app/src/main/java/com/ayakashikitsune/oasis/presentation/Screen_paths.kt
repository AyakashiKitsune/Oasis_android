package com.ayakashikitsune.oasis.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.ManageSearch
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen_paths(val title: String, val address_id: String, val icon: ImageVector) {
    //    overview, sales, inventory, settings
    object Overview_screen : Screen_paths("Overview", "overview_Screen", Icons.Default.Home)
    object Sales_screen : Screen_paths("Sales", "sales_Screen", Icons.Default.PointOfSale) {
        val tabs = listOf(
            Tabs("Sales graph", Icons.Rounded.PointOfSale),
//            SalesTabs("", Icons.Rounded.FormatListNumbered),
            Tabs("Predict: \nSold Item", Icons.Rounded.AutoGraph),
            Tabs("Product ranks", Icons.Rounded.FormatListNumbered),
        )
    }

    object Inventory_screen : Screen_paths("Inventory", "inventory_Screen", Icons.Default.Inventory){
        val tabs = listOf(
            Tabs("Sell or Stop",Icons.Rounded.Sell),
            Tabs("A.P.I.S analysis, promote, inventory,suggestion", Icons.Rounded.ManageSearch),
            Tabs("Reorder", Icons.Rounded.Reorder)
        )
    }
    object Settings_screen : Screen_paths("Settings", "settings_Screen", Icons.Default.Settings)
    object About_screen : Screen_paths("About", "About_Screen", Icons.Default.PermDeviceInformation)
    object Error_Screen : Screen_paths("Error Logs", "ErrorScreen", Icons.Default.ErrorOutline)
}

data class Tabs(
    val name: String,
    val icon: ImageVector,
)

val navItems = listOf(
    Screen_paths.Overview_screen,
    Screen_paths.Sales_screen,
    Screen_paths.Inventory_screen,
    Screen_paths.Settings_screen,
)