package com.ayakashikitsune.oasis.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.jsonModels.StocknalisysResponse_model
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.ayakashikitsune.oasis.utils.converters.digit_month
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inventory_Screen(
    viewmodel: OASISViewmodel,
    paddingValues: PaddingValues
) {
    val inventoryState by viewmodel.inventoryState.collectAsState()
    val indexTab by remember {
        derivedStateOf {
            inventoryState.indexTab
        }
    }
    var isLoading by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }


    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val tabs = listOf("My Inventory", "StocksNalysis", "Save/Kill analysis")
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = tabs[indexTab],
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                TabRow(selectedTabIndex = indexTab) {
                    tabs.forEachIndexed { index, text ->
                        Tab(text = {
                            Text(
                                text = text, style = MaterialTheme.typography.titleMedium
                            )
                        }, selected = index == indexTab, onClick = {
                            viewmodel.inventoryUpdate(
                                inventoryState.onchangeIndex(index)
                            )
                        })
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.padding(paddingValues)
    ) {
        when (indexTab) {
            0 -> InventoryTable(viewmodel)
            1 -> StockNalysis(
                viewmodel = viewmodel,
                onquery = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            isLoading = true
                            dialogTitle = "getting stock analysis"
                            delay(1500)
                            viewmodel.get_stocks_analysis(
                                onError = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            it,
                                            duration = SnackbarDuration.Long
                                        )
                                        isLoading = false
                                    }
                                }
                            )
                            dialogTitle = "got the stock analysis"
                            while (inventoryState.listofStocknalysis == null) {
                                dialogTitle = "showing stock analysis"
                                delay(1000)
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )

            2 -> SavekillTable(
                viewmodel = viewmodel,
                onquery = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            isLoading = true
                            dialogTitle = "getting Save / kill analysis"
                            delay(1500)
                            viewmodel.get_savekill_analysis(
                                onError = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            it,
                                            duration = SnackbarDuration.Long
                                        )
                                        isLoading = false
                                    }
                                }
                            )
                            dialogTitle = "got the Save / kill analysis"
                            while (inventoryState.listofSaveKill == null) {
                                dialogTitle = "showing Save / kill analysis"
                                delay(1000)
                            }
                            isLoading = false
                        }
                    }

                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    }

    if (isLoading) {
        AlertDialog(
            onDismissRequest = { isLoading = false },
            title = {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                )
            },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { isLoading = false }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }
}

@Composable
fun InventoryTable(
    viewmodel: OASISViewmodel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewmodel.test_error()
            }
        ) {
          Text(text = "error btn")
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StockNalysis(
    viewmodel: OASISViewmodel,
    onquery: () -> Unit,
    modifier: Modifier
) {
    val inventoryState by viewmodel.inventoryState.collectAsState()
    val listOfStocknalysis by remember {
        derivedStateOf {
            inventoryState.listofStocknalysis
        }
    }
    val config = LocalConfiguration.current
    val scrollState = rememberScrollState()

    LaunchedEffect(true) {
        withContext(Dispatchers.IO) {
            onquery()
        }
    }

    Surface(
        modifier = modifier
    ) {
        if (listOfStocknalysis == null) {
            Surface {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
                            text = "Empty Stock analysis",
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                stickyHeader {
                    val list = listOf(
                        "name", "january", "february", "march", "april", "may", "june",
                        "july", "august", "september", "october", "november", "december",
                        "min", "max", "average"
                    )
                    val currentMonth = digit_month[YearMonth.now().monthValue]
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .horizontalScroll(scrollState)
                    ) {
                        list.forEach {
                            Surface(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .requiredWidth(config.screenWidthDp.dp / 3),
                                color = if (it == currentMonth) MaterialTheme.colorScheme.tertiaryContainer
                                else MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                items(
                    count = listOfStocknalysis!!.size,
                    key = {
                        val item = listOfStocknalysis!![it]
                        item.id
                    }
                ) {
                    StocknalysisRow(
                        item = listOfStocknalysis!![it],
                        scrollState = scrollState,
                        width = config.screenWidthDp.dp,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
fun StocknalysisRow(
    item: StocknalisysResponse_model,
    scrollState: ScrollState,
    width: Dp,
    modifier: Modifier
) {
    val data = listOf(
        item.name,
        item.january, item.february, item.march, item.april,
        item.may, item.june, item.july, item.august,
        item.september, item.october, item.november, item.december,
        item.min, item.max, item.average
    )
    Surface(
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in data) {
                Text(
                    text = "${i}",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .requiredWidth(width / 3)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavekillTable(
    viewmodel: OASISViewmodel,
    onquery: () -> Unit,
    modifier: Modifier
) {
    val inventoryState by viewmodel.inventoryState.collectAsState()
    val currentMonth = YearMonth.now().monthValue
    val config = LocalConfiguration.current
    val continueSell by remember {
        derivedStateOf {
            inventoryState.listofSaveKill?.filter {
                when (currentMonth) {
                    1 -> it.january == 1
                    2 -> it.february == 1
                    3 -> it.march == 1
                    4 -> it.april == 1
                    5 -> it.may == 1
                    6 -> it.june == 1
                    7 -> it.july == 1
                    8 -> it.august == 1
                    9 -> it.september == 1
                    10 -> it.october == 1
                    11 -> it.november == 1
                    12 -> it.december == 1
                    else -> {
                        it.january == 1
                    }
                }
            }
        }
    }
    val notsell by remember {
        derivedStateOf {
            inventoryState.listofSaveKill?.filterNot {
                when (currentMonth) {
                    1 -> it.january == 1
                    2 -> it.february == 1
                    3 -> it.march == 1
                    4 -> it.april == 1
                    5 -> it.may == 1
                    6 -> it.june == 1
                    7 -> it.july == 1
                    8 -> it.august == 1
                    9 -> it.september == 1
                    10 -> it.october == 1
                    11 -> it.november == 1
                    12 -> it.december == 1
                    else -> {
                        it.january == 1
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        withContext(Dispatchers.IO) {
            onquery()
        }
    }

    if (continueSell == null) {
        Surface(
            modifier = modifier
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
                        text = "Empty Stock analysis",
                        style = MaterialTheme.typography.displayMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
        ) {
            stickyHeader {
                Surface {
                    CardWithTitle(
                        title = "Continue to sell for this month of ${digit_month[currentMonth]}",
                        modifier = Modifier
                    ) {
                        Text(
                            text = "these are the recommended items you can sell this month in order to gain sale than hoarding stocks in you inventory",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
            items(continueSell!!.size) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = continueSell!![it].name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            stickyHeader {
                Surface {
                    CardWithTitle(
                        title = "Stop to selling these items month of ${digit_month[currentMonth]}",
                        modifier = Modifier
                    ) {
                        Text(
                            text = "these are the item were not popular based on your sales data. you either sell the remaining displayed product or remove it and replace it with recommended to sell",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            }
            items(notsell!!.size) {
                Surface(
                    color = Color(239, 41, 23),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = notsell!![it].name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}