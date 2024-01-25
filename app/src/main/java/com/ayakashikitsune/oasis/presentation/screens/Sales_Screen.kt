package com.ayakashikitsune.oasis.presentation.screens

import android.icu.util.Calendar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.constants.SalesResponse_model_sort
import com.ayakashikitsune.oasis.data.jsonModels.Query_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.ayakashikitsune.oasis.utils.converters.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sales_Screen(
    viewmodel: OASISViewmodel,
    paddingValues: PaddingValues
) {
    var iswholesale by remember { mutableStateOf(false) }
    var isGraphView by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutine = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val tabs = listOf("My Sales", "Prediction")
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
                            text = tabs[selectedTabIndex],
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                showBottomSheet = true
                                coroutine.launch {
                                    bottomSheetState.partialExpand()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FilterAlt, contentDescription = "filter"
                            )
                            Text(text = "Filter", style = MaterialTheme.typography.labelLarge)
                        }
                        TextButton(
                            onClick = {
//                                viewmodel
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null
                            )
                            Text(text = "Refresh", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, text ->
                        Tab(text = {
                            Text(
                                text = text, style = MaterialTheme.typography.titleMedium
                            )
                        }, selected = index == selectedTabIndex, onClick = {
                            selectedTabIndex = index
                        })
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.padding(paddingValues)
    ) { padding ->
        when (selectedTabIndex) {
            0 -> {
                MySalesTable(
                    viewmodel = viewmodel,
                    iswholesale = { iswholesale },
                    isGraphView = { isGraphView },
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
    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                coroutine.launch {
                    bottomSheetState.hide()
                }
                showBottomSheet = false
            },
        ) {
            FilterSheet(
                iswholesale = { iswholesale },
                isGraphView = { isGraphView },
                onChangeisGraphView = { isGraphView = it },
                onChangeiswholesale = { iswholesale = it },
                closeSheet = {
                    coroutine.launch {
                        bottomSheetState.hide()
                        delay(5)
                        showBottomSheet = false
                    }
                },
                applyFilter = {
                    viewmodel.evaluateSalesQuery(
                        queryModel = it,
                        onError = {
                            coroutine.launch {
                                snackbarHostState.showSnackbar(
                                    message = it,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    iswholesale: () -> Boolean,
    isGraphView: () -> Boolean,
    onChangeiswholesale: (Boolean) -> Unit,
    onChangeisGraphView: (Boolean) -> Unit,
    closeSheet: () -> Unit,
    applyFilter: (Query_model) -> Unit,
    modifier: Modifier
) {
    var isMultipleDate by remember { mutableStateOf(false) }
    var isDateInputMode by remember { mutableStateOf(false) }
    var justRecentSales by remember { mutableStateOf(false) }

    var datePicked by remember { mutableStateOf("") }
    var dateRangeStart by remember { mutableStateOf("None") }
    var dateRangeEnd by remember { mutableStateOf("None") }

    val calendar = Calendar.getInstance()
    val dateOnly = rememberDatePickerState(
        initialDisplayedMonthMillis = calendar.timeInMillis,
    )
    val dateRange = rememberDateRangePickerState(
        initialDisplayedMonthMillis = calendar.timeInMillis,
    )


    LaunchedEffect(dateOnly.selectedDateMillis) {
        withContext(Dispatchers.IO) {
            datePicked = dateOnly.selectedDateMillis.let {
                if (it == null) {
                    "None"
                } else {
                    it.toDate()
                }
            }
        }
    }
    LaunchedEffect(
        key1 = dateRange.selectedStartDateMillis,
        key2 = dateRange.selectedEndDateMillis,
    ) {
        withContext(Dispatchers.IO) {
            dateRangeStart = dateRange.selectedStartDateMillis.let {
                if (it == null) {
                    "None"
                } else {
                    it.toDate()
                }
            }
            dateRangeEnd = dateRange.selectedEndDateMillis.let {
                if (it == null) {
                    "None"
                } else {
                    it.toDate()
                }
            }
        }
    }


    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .padding(12.dp)
    ) {
        item(key = "close or apply") {
            Surface {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedButton(onClick = {
                        closeSheet()
                    }) {
                        Text(text = "Cancel")
                    }
                    FilledTonalButton(onClick = {
                        applyFilter(
                            Query_model(
                                datePicked = datePicked,
                                iswholesale = iswholesale(),
                                isMultipleDate = isMultipleDate,
                                dateRangeStart = dateRangeStart,
                                dateRangeEnd = dateRangeEnd,
                                justRecentSales = justRecentSales
                            )
                        )
                        closeSheet()
                    }) {
                        Text(text = "Apply")
                    }
                }
            }
        }
        item(key = "graph view mode") {
            SwitchTile(
                value = { isGraphView() },
                onChangeValue = { onChangeisGraphView(it) },
                title = "Graph View mode",
                subtitle = {
                    if (isGraphView()) "You selected to view your sales in Line graph"
                    else "You selected to view your sales in table," +
                            " you can enable this if Group sales per day is on"
                },
                enableButton = { iswholesale() },
                modifier = Modifier
            )
        }
        item(key = "wholesale switch") {
            SwitchTile(
                value = { iswholesale() },
                onChangeValue = { onChangeiswholesale(it) },
                title = "Group sales per day",
                subtitle = {
                    if (iswholesale()) "You selected to group sale by day"
                    else "You selected to query all the Sales ungrouped"
                },
                modifier = Modifier
            )
        }
        item(key = "recent switch") {
            SwitchTile(
                value = { justRecentSales },
                onChangeValue = { justRecentSales = it },
                title = "Quick Query Recent Sale",
                subtitle = {
                    if (justRecentSales) "You selected Recent Sales to quickly query it, no more date picking"
                    else "You selected manually selecting date"
                },
                modifier = Modifier
            )
        }
        if (!justRecentSales) {
            item(key = "multiple date mode") {
                SwitchTile(
                    value = { isMultipleDate },
                    onChangeValue = { isMultipleDate = it },
                    title = "Multiple Date Selection Mode",
                    subtitle = {
                        if (isMultipleDate) "You selected multiple date"
                        else "You selected single date"
                    },
                    modifier = Modifier
                )
            }
            item(key = "date input mode") {
                SwitchTile(
                    value = { isDateInputMode },
                    onChangeValue = {
                        isDateInputMode = it
                        when (it) {
                            true -> {
                                dateOnly.displayMode = DisplayMode.Input
                                dateRange.displayMode = DisplayMode.Input
                            }

                            false -> {
                                dateOnly.displayMode = DisplayMode.Picker
                                dateRange.displayMode = DisplayMode.Picker
                            }
                        }
                    },
                    title = "Switch Date Input Mode",
                    subtitle = {
                        if (isDateInputMode) "You selected Text Mode to type the date specifically"
                        else "You selected Date Mode to select date"
                    },
                    modifier = Modifier
                )
            }
            item(key = "text selected dates") {
                Card {
                    Text(
                        text = "Selected Date",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = if (isMultipleDate) "Starting Date : ${dateRangeStart}\n" +
                                "Ending Date : ${dateRangeEnd}"
                        else "Date : ${datePicked}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )
                }
            }
            item(key = "calendars") {
                Card() {
                    when (isMultipleDate) {
                        true -> {
                            DateRangePicker(
                                state = dateRange,
                                modifier = Modifier
                                    .then(
                                        if (isDateInputMode) {
                                            Modifier.aspectRatio(3f/2)
                                        } else {
                                            Modifier.height(height = 600.dp)
                                        }
                                    )
                                    .padding(12.dp)
                            )
                        }

                        false -> {
                            DatePicker(
                                state = dateOnly, modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwitchTile(
    value: () -> Boolean,
    onChangeValue: (Boolean) -> Unit,
    title: String,
    subtitle: () -> String,
    enableButton: () -> Boolean = { true },
    modifier: Modifier
) {
    Card(
        modifier = modifier.then(
            if (enableButton()) {
                Modifier.clickable {
                    onChangeValue(!value())
                }
            } else {
                Modifier
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (enableButton()) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.inverseOnSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
                Text(
                    text = subtitle(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                )
            }

            Switch(
                checked = value(),
                onCheckedChange = {
                    onChangeValue(it)
                },
                enabled = enableButton()
            )
        }


    }
}


@Composable
fun MySalesPrediction(
    modifier: Modifier
) {

    Surface(
        modifier = modifier, color = Color.Green
    ) {

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MySalesTable(
    viewmodel: OASISViewmodel,
    iswholesale: () -> Boolean,
    isGraphView: () -> Boolean,
    modifier: Modifier,
) {
    val salesState = viewmodel.salesState.collectAsState()
    var sortby by remember { mutableStateOf<SalesResponse_model_sort>(SalesResponse_model_sort.DATE) }
    var orderByASC by remember { mutableStateOf(true) }
    val listOfSales by remember(iswholesale()) {
        derivedStateOf {
            if (iswholesale()) {
                println("whosales")
                salesState.value.listSalesWholesaleCache
            } else {
                salesState.value.listSalesCache
            }
        }
    }
    val config = LocalConfiguration.current
    val scrollState = rememberScrollState()
    val iconASC = if (orderByASC) {
        Icons.Rounded.ArrowUpward
    } else {
        Icons.Rounded.ArrowDownward
    }

    Surface(
        modifier = modifier
    ) {
        if (listOfSales.isEmpty()) {
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
            if (isGraphView() and iswholesale()) {
                PlotDate(
                    x = listOfSales.map { (it as SalesWholesaleResponse_model).date },
                    y = listOfSales.map { (it as SalesWholesaleResponse_model).sum },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    stickyHeader {
                        val list = if (iswholesale()) listOf("Date", "Sale")
                        else listOf("Id", "Name", "Category", "Price", "Date", "Sale")
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .horizontalScroll(scrollState)
                        ) {
                            list.forEach {
                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .requiredWidth(config.screenWidthDp.dp / 2)
                                        .clickable {
                                            orderByASC = !orderByASC
                                            when (it) {
                                                "Id" -> {
                                                    sortby = SalesResponse_model_sort.ID
                                                }

                                                "Name" -> {
                                                    sortby = SalesResponse_model_sort.NAME
                                                }

                                                "Category" -> {
                                                    sortby = SalesResponse_model_sort.CATEGORY
                                                }

                                                "Date" -> {
                                                    sortby = SalesResponse_model_sort.DATE
                                                }

                                                "Price" -> {
                                                    sortby = SalesResponse_model_sort.PRICE
                                                }

                                                "Sale" -> {
                                                    sortby = SalesResponse_model_sort.SALE
                                                }
                                            }
                                            viewmodel.sort(
                                                iswholesale = iswholesale(),
                                                sortby = sortby,
                                                orderByASC = orderByASC
                                            )
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (it.lowercase() == sortby.name.lowercase()) {
                                        Icon(
                                            imageVector = iconASC, contentDescription = null
                                        )
                                    }
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }
                    items(
                        count = listOfSales.size,
                        key = {
                            val item = listOfSales[it]
                            if (item is SalesResponse_model) {
                                item.id
                            } else {
                                val cast = listOfSales.get(it) as SalesWholesaleResponse_model
                                cast.date
                            }
                        }
                    ) {
                        RowTable(
                            item = listOfSales[it],
                            scrollState = scrollState,
                            width = config.screenWidthDp.dp,
                            modifier = Modifier,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RowTable(
    item: Any,
    scrollState: ScrollState,
    width: Dp,
    modifier: Modifier
) {
    var data = listOf<Any>()
    if (item is SalesResponse_model) {
        data = listOf(item.id, item.name, item.category, item.price, item.date, item.sale)
    }
    if (item is SalesWholesaleResponse_model) {
        data = listOf(item.date, item.sum)
    }
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
            if (item is SalesResponse_model) {
                Text(
                    text = data[0].toString(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.requiredWidth(width/2).padding(start = 8.dp)
                )
                for (i in 1..data.size - 2) {
                    Text(
                        text = "${data[i]}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.requiredWidth(width / 2).padding(start = 8.dp)
                    )
                }
                Card(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .requiredWidth(width / 2)
                ) {
                    Text(
                        text = "${data.last()}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else{
                Text(
                    text = "${data.first()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.requiredWidth(width / 2).padding(start = 8.dp)
                )
                Card(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .requiredWidth(width / 2)
                ) {
                    Text(
                        text = "${data.last()}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }



        }
    }
}