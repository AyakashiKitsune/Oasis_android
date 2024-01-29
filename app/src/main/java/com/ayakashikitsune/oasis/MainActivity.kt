package com.ayakashikitsune.oasis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.ayakashikitsune.oasis.presentation.Screen_paths
import com.ayakashikitsune.oasis.presentation.Tabs
import com.ayakashikitsune.oasis.presentation.navItems
import com.ayakashikitsune.oasis.presentation.screens.About_Screen
import com.ayakashikitsune.oasis.presentation.screens.ErrorScreen
import com.ayakashikitsune.oasis.presentation.screens.Inventory_Screen
import com.ayakashikitsune.oasis.presentation.screens.Overview_Screen
import com.ayakashikitsune.oasis.presentation.screens.Sales_Screen
import com.ayakashikitsune.oasis.presentation.screens.Settings_Screen
import com.ayakashikitsune.oasis.presentation.screens.WelcomeScreen
import com.ayakashikitsune.oasis.ui.theme.OASISTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OASISTheme {
                val viewmodel = OASISViewmodel()
                MainScreen(viewmodel, Modifier.fillMaxSize())
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewmodel: OASISViewmodel,
    modifier: Modifier
) {
    val navController = rememberNavController()
    val currentNav = navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    var iswelcomed by remember { mutableStateOf<Boolean>(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        iswelcomed = viewmodel.getIswelcome(context)
    }
    Scaffold(
        bottomBar = {
            if(currentNav.value?.destination?.route != Screen_paths.Welcome_screen.address_id){
                Oasis_NavigationBar(navList = navItems, onClick = {
                    navController.navigate(it.address_id) {
                        this.launchSingleTop = true
                    }
                }, selected = {
                    currentNav.value?.destination?.route ?: Screen_paths.Overview_screen.address_id
                })
            }
        },
        modifier = modifier
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen_paths.Welcome_screen.address_id,
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
                Sales_Screen(
                    viewmodel,
                    padding
                )
            }
            composable(Screen_paths.Inventory_screen.address_id) {
                Inventory_Screen(
                    viewmodel,
                    padding
                )
            }
            composable(Screen_paths.Settings_screen.address_id) {
                Settings_Screen(
                    navController
                )
            }
            composable(Screen_paths.About_screen.address_id) {
                About_Screen()
            }
            composable(Screen_paths.Error_screen.address_id) {
                ErrorScreen(viewmodel, Modifier.fillMaxSize())
            }
            composable(Screen_paths.Welcome_screen.address_id) {
                WelcomeScreen(
                    moveNextScreen = {
                        coroutineScope.launch {
                            withContext(Dispatchers.IO){
                                iswelcomed = viewmodel.setIswelcome(context)
                            }
                        }
                        println(iswelcomed)
                        if (iswelcomed == true) {
                            navController.navigate(Screen_paths.Overview_screen.address_id) {
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
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
