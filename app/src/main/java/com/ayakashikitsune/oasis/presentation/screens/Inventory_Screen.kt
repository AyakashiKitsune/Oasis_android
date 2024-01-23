package com.ayakashikitsune.oasis.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Inventory_Screen(
    paddingValues: PaddingValues,
    currentPage : ()->Int,
    horizontalpager: () -> PagerState
) {

    HorizontalPager(
        state = horizontalpager(),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues),
            color = Color.Blue
        ) {
            when (currentPage()) {
                0 -> Sell_Kill_inventory()
                1 -> APIS_Inventory()
                2 -> Reorder_Inventory()
                else -> Box(Modifier.fillMaxSize()) {

                }
            }
        }
    }
}

@Composable
fun Sell_Kill_inventory() {
    val tabMod = Modifier.fillMaxWidth()
    val headers = listOf("Sell", "Stop Selling")
    LazyColumn{
       headers.forEach{

       }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun APIS_Inventory() {
    val tabMod = Modifier.fillMaxWidth()
    LazyColumn {
        stickyHeader() {
            Card(
                modifier = tabMod
            ) {
                Text(text = "Bundle", modifier = Modifier.fillMaxWidth())
            }
        }
        items(40) {
            Row {
                Text(text = "$it", modifier = Modifier.fillMaxWidth())
            }
        }
        stickyHeader {
            Card(modifier = tabMod) {
                Text(text = "Price Optimization")
            }
        }
        items(40) {
            Row {
                Text(text = "$it", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Reorder_Inventory() {
    val tabMod = Modifier.fillMaxWidth()
    LazyColumn {
        stickyHeader() {
            Card(
                modifier = tabMod
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Name", Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(text = "Current Stock", Modifier.weight(1f), textAlign = TextAlign.Center)
                    Text(
                        text = "Expected warning Level",
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        items(40) {
            Row {
                Text(text = "$it", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}