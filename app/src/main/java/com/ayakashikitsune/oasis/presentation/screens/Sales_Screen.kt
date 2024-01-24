package com.ayakashikitsune.oasis.presentation.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.constants.SalesResponse_model_sort
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.model.OASISViewmodel


@Composable
fun Sales_Screen(
    viewmodel: OASISViewmodel
) {
    val salesState = viewmodel.salesState.collectAsState()

    val listofSalesState by remember {
        derivedStateOf {
            salesState.value.listPredictedWholeSalesCache
        }
    }
    var sortrecentby by remember { mutableStateOf<SalesResponse_model_sort>(SalesResponse_model_sort.ID) }
    var orderByASC by remember { mutableStateOf(true) }
    val listOfRecentSales by remember {
        derivedStateOf {
            val result = when (sortrecentby) {
                SalesResponse_model_sort.CATEGORY -> {
                    salesState.value.recentSalesCache.sortedBy {
                        it.category
                    }
                }

                SalesResponse_model_sort.DATE -> {
                    salesState.value.recentSalesCache.sortedBy {
                        it.date
                    }
                }

                SalesResponse_model_sort.ID -> {
                    salesState.value.recentSalesCache.sortedBy {
                        it.id
                    }
                }

                SalesResponse_model_sort.NAME -> {
                    salesState.value.recentSalesCache.sortedBy {
                        it.price
                    }
                }

                SalesResponse_model_sort.PRICE -> {
                    salesState.value.recentSalesCache.sortedBy {
                        it.price
                    }
                }

                SalesResponse_model_sort.SALE -> {
                    salesState.value.recentSalesCache.sortedBy {
                        it.sale
                    }
                }
            }
            if (orderByASC) {
                result
            } else {
                result.reversed()
            }
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
//    var showDialog by remember{ mutableStateOf(false) }
//    var messageDialog by remember{ mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            val tabs = listOf("My Sales", "Prediction")
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        text = { Text(text = text, style = MaterialTheme.typography.titleMedium) },
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                        }
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { padding ->
        when (selectedTabIndex) {
            0 -> {
                MySalesTable(
                    listOfRecentSales = { listOfRecentSales },
                    onChangeOrderByASC = { orderByASC = !orderByASC },
                    onChangeSortrecentby = { sortrecentby = it },
                    getOrderByASC = { orderByASC },
                    getSortrecentby = { sortrecentby },
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }

            1 -> {
                MySalesPrediction(modifier = Modifier)
            }
        }
    }
}

@Composable
fun MySalesPrediction(
    modifier: Modifier
) {

    Surface(
        modifier = modifier,
        color = Color.Green
    ) {

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MySalesTable(
    listOfRecentSales: () -> List<SalesResponse_model>,
    onChangeOrderByASC: () -> Unit,
    getOrderByASC: () -> Boolean,
    onChangeSortrecentby: (SalesResponse_model_sort) -> Unit,
    getSortrecentby: () -> SalesResponse_model_sort,
    modifier: Modifier,
) {
    val config = LocalConfiguration.current
    val iconASC = if (getOrderByASC()) {
        Icons.Rounded.ArrowUpward
    } else {
        Icons.Rounded.ArrowDownward
    }

    Surface(
        modifier = modifier
    ) {
        if (listOfRecentSales().isEmpty()) {
            Surface {
                Box(contentAlignment = Alignment.Center) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(config.screenWidthDp.dp / 2)
                        )
                        Text(
                            text = "You have no sales",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState {
                            Log.d("swipe", it.toString())
                        }
                    )
            ) {
                stickyHeader {
                    val list = listOf("Id", "Name", "Category", "Date", "Price", "Sale")
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .scrollable(
                                rememberScrollState(),
                                orientation = Orientation.Horizontal,
                                enabled = true
                            )
                    ) {
                        list.forEach {
                            if (it.lowercase() == getSortrecentby().name.lowercase()) {
                                Icon(
                                    imageVector = iconASC,
                                    contentDescription = null
                                )
                            }
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .width(config.screenWidthDp.dp / 4)
                                    .clickable {
                                        onChangeOrderByASC()
                                        when (it) {
                                            "Id" -> {
                                                onChangeSortrecentby(SalesResponse_model_sort.ID)
                                            }

                                            "Name" -> {
                                                onChangeSortrecentby(SalesResponse_model_sort.NAME)
                                            }

                                            "Category" -> {
                                                onChangeSortrecentby(SalesResponse_model_sort.CATEGORY)
                                            }

                                            "Date" -> {
                                                onChangeSortrecentby(SalesResponse_model_sort.DATE)
                                            }

                                            "Price" -> {
                                                onChangeSortrecentby(SalesResponse_model_sort.PRICE)
                                            }

                                            "Sale" -> {
                                                onChangeSortrecentby(SalesResponse_model_sort.SALE)
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
                items(listOfRecentSales().size, key = { listOfRecentSales().get(it).id }) {
                    RowTable(
                        item = listOfRecentSales()[it],
                        width = config.screenWidthDp.dp * 0.1f,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}


@Composable
fun RowTable(
    item: SalesResponse_model,
    width: Dp,
    modifier: Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .border(
                        color = MaterialTheme.colorScheme.primary,
                        width = 1.dp,
                        shape = CircleShape
                    )
                    .size(width)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.id.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.category,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.date,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.price.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.sale.toString(),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}