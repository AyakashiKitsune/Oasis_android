package com.ayakashikitsune.oasis

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.ayakashikitsune.oasis.presentation.Screen_paths
import com.ayakashikitsune.oasis.presentation.Tabs
import com.ayakashikitsune.oasis.presentation.navItems
import com.ayakashikitsune.oasis.presentation.screens.About_Screen
import com.ayakashikitsune.oasis.presentation.screens.Inventory_Screen
import com.ayakashikitsune.oasis.presentation.screens.Overview_Screen
import com.ayakashikitsune.oasis.presentation.screens.Sales_Screen
import com.ayakashikitsune.oasis.presentation.screens.Settings_Screen
import com.ayakashikitsune.oasis.ui.theme.OASISTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OASISTheme {
                val viewmodel = OASISViewmodel()
                MainScreen(viewmodel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewmodel : OASISViewmodel
) {
    val navController = rememberNavController()
    val currentNav = navController.currentBackStackEntryAsState()

    var currentPage by remember { mutableIntStateOf(0) }
    val inventoryHorizontalState = rememberPagerState(initialPage = 0, pageCount = { Screen_paths.Sales_screen.tabs.size })

    Scaffold(
        bottomBar = {
            Oasis_NavigationBar(navList = navItems, onClick = {
                navController.navigate(it.address_id){
                    this.launchSingleTop = true
                }
            }, selected = {
                currentNav.value?.destination?.route ?: Screen_paths.Overview_screen.address_id
            })
        },
    ) {  padding ->
        NavHost(
            navController = navController,
            startDestination = Screen_paths.Overview_screen.address_id,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popExitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() }
        ) {

            composable(Screen_paths.Overview_screen.address_id) {
                Overview_Screen(
                    viewmodel,
                    padding
                )
            }
            composable(Screen_paths.Sales_screen.address_id) {
                Sales_Screen(viewmodel)
            }
            composable(Screen_paths.Inventory_screen.address_id) {
                Inventory_Screen(
                    paddingValues = padding,
                    currentPage = {currentPage},
                    horizontalpager = {inventoryHorizontalState}
                )
            }
            composable(Screen_paths.Settings_screen.address_id) {
                Settings_Screen()
            }
            composable(Screen_paths.About_screen.address_id) {
                About_Screen()
            }
        }
    }
}

@Composable
fun MainTabs(
    list: List<Tabs>,
    getCurrentPage: () -> Int,
    pageIndex: (index: Int) -> Unit,
) {
    TabRow(selectedTabIndex = getCurrentPage()) {
        list.forEachIndexed { index, tab ->
            Tab(
                selected = getCurrentPage() == index,
                onClick = {
                    pageIndex(index)
                },
                text = { Text(tab.name) },
                icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}


@Composable
fun Oasis_NavigationBar(
    navList: List<Screen_paths>, onClick: (Screen_paths) -> Unit, selected: () -> String
) {
    NavigationBar {
        navList.forEach {
            NavigationBarItem(label = { Text(text = it.title) },
                selected = selected() == it.address_id,
                onClick = { onClick(it) },
                icon = { Icon(imageVector = it.icon, contentDescription = it.title) })
        }
    }
}
