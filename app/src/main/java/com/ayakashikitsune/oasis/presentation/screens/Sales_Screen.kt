package com.ayakashikitsune.oasis.presentation.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.constants.SalesResponse_model_sort
import com.ayakashikitsune.oasis.data.jsonModels.PredictWholesalesRequest_model
import com.ayakashikitsune.oasis.data.jsonModels.Query_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.ayakashikitsune.oasis.utils.converters.FromHelperThatDateStrjustYear
import com.ayakashikitsune.oasis.utils.converters.FromHelpertoDate
import com.ayakashikitsune.oasis.utils.converters.FromHelpertoTimemilis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Sales_Screen(
    viewmodel: OASISViewmodel,
    paddingValues: PaddingValues
) {
    var iswholesale by remember { mutableStateOf(false) }
    var isGraphView by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("title") }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val salesState by viewmodel.salesState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val mindate by remember {
        derivedStateOf {
            salesState.min_date
        }
    }
    val maxdate by remember {
        derivedStateOf {
            salesState.max_date
        }
    }

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
                                coroutineScope.launch {
                                    bottomSheetState.partialExpand()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FilterAlt, contentDescription = "filter"
                            )
                            Text(text = "Filter", style = MaterialTheme.typography.labelLarge)
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
                MySalesPrediction(
                    viewmodel = viewmodel,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }
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

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
                showBottomSheet = false
            },
        ) {
            when(selectedTabIndex){
                0 -> {
                    FilterSheet(
                        iswholesale = { iswholesale },
                        isGraphView = { isGraphView },
                        maxdate = maxdate,
                        mindate = mindate,
                        onChangeisGraphView = { isGraphView = it },
                        onChangeiswholesale = { iswholesale = it },
                        closeSheet = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                delay(5)
                                showBottomSheet = false
                            }
                        },
                        applyFilter = {
                            coroutineScope.launch {
                                isLoading = true
                                dialogTitle = buildString {
                                    append("Querying your ")
                                    if (isGraphView) {
                                        append("Graphed ")
                                    }
                                    if (iswholesale) {
                                        append("Wholesales Grouped")
                                    }
                                    append("Sales ")
                                    if (it.justRecentSales) {
                                        append("with Quick recent query")
                                    } else {
                                        if (it.isMultipleDate) {
                                            append("\nbetween dates -> ${it.dateRangeStart}")
                                            append(", ${it.dateRangeEnd}")
                                        } else {
                                            append("on date -> ${it.datePicked}")
                                        }
                                    }
                                }
                                delay(1000)
                                viewmodel.evaluateSalesQuery(
                                    queryModel = it,
                                    onError = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = it,
                                                duration = SnackbarDuration.Long
                                            )
                                            dialogTitle = "Error encountered"
                                            isLoading = false
                                        }
                                    }
                                )
                                dialogTitle = "Got your query"
                                delay(1000)
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                1 -> {
                    FilterSheetPrediction(
                        isGraphView = {isGraphView},
                        maxdate = maxdate,
                        mindate = mindate,
                        onChangeisGraphView = { isGraphView = it },
                        closeSheet = {
                            coroutineScope.launch {
                                bottomSheetState.hide()
                                delay(5)
                                showBottomSheet = false
                            }
                        },
                        applyFilter = {
                            coroutineScope.launch {
                                isLoading = true
                                dialogTitle = "predicting your ${it.duration} days from ${mindate.FromHelpertoDate()}"
                                delay(1000)
                                viewmodel.predict_wholesales(
                                    duration = it.duration,
                                    onError = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = it,
                                                duration = SnackbarDuration.Long
                                            )
                                            dialogTitle = "Error encountered"
                                            isLoading = false
                                        }
                                    }
                                )
                                dialogTitle = "Got your prediction"
                                delay(1000)
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    iswholesale: () -> Boolean,
    isGraphView: () -> Boolean,
    maxdate: String,
    mindate: String,
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

    val dateOnly = rememberDatePickerState(
        yearRange = IntRange(
            mindate.FromHelpertoDate().FromHelperThatDateStrjustYear(),
            maxdate.FromHelpertoDate().FromHelperThatDateStrjustYear()
        ),
        initialSelectedDateMillis = mindate.FromHelpertoDate().FromHelpertoTimemilis(),
        initialDisplayedMonthMillis = mindate.FromHelpertoDate().FromHelpertoTimemilis(),
    )
    val dateRange = rememberDateRangePickerState(
        yearRange = IntRange(
            mindate.FromHelpertoDate().FromHelperThatDateStrjustYear(),
            maxdate.FromHelpertoDate().FromHelperThatDateStrjustYear()
        ),
        initialSelectedStartDateMillis = mindate.FromHelpertoDate().FromHelpertoTimemilis(),
        initialDisplayedMonthMillis = mindate.FromHelpertoDate().FromHelpertoTimemilis(),
    )


    LaunchedEffect(dateOnly.selectedDateMillis) {
        withContext(Dispatchers.IO) {
            datePicked = dateOnly.selectedDateMillis.let {
                if (it == null) {
                    "None"
                } else {
                    it.FromHelpertoDate()
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
                    it.FromHelpertoDate()
                }
            }
            dateRangeEnd = dateRange.selectedEndDateMillis.let {
                if (it == null) {
                    "None"
                } else {
                    it.FromHelpertoDate()
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
                onChangeValue = {
                    justRecentSales = it
                    if (it) {
                        isMultipleDate = false
                        isDateInputMode = false
                    }
                },
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
                                            Modifier.aspectRatio(3f / 2)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheetPrediction(
//    iswholesale: () -> Boolean,
    isGraphView: () -> Boolean,
    maxdate: String,
    mindate: String,
    onChangeisGraphView: (Boolean) -> Unit,
    closeSheet: () -> Unit,
    applyFilter: (PredictWholesalesRequest_model) -> Unit,
    modifier: Modifier
) {
    var isDateInputMode by remember { mutableStateOf(false) }

    var dateRangeStart by remember { mutableStateOf("None") }
    var dateRangeEnd by remember { mutableStateOf("None") }

    var initialStartdate by remember {
        mutableStateOf(maxdate.FromHelpertoDate().FromHelpertoTimemilis())
    }
    val dateRange = rememberDateRangePickerState(
        yearRange = IntRange(
            maxdate.FromHelpertoDate().FromHelperThatDateStrjustYear(),
            DatePickerDefaults.YearRange.last
        ),
        initialSelectedStartDateMillis = initialStartdate,
        initialDisplayedMonthMillis = mindate.FromHelpertoDate().FromHelpertoTimemilis(),
    )


    LaunchedEffect(
        key1 = dateRange.selectedStartDateMillis,
        key2 = dateRange.selectedEndDateMillis,
    ) {
        withContext(Dispatchers.IO) {
            dateRangeStart = mindate.FromHelpertoDate()
            initialStartdate = dateRangeStart.FromHelpertoTimemilis()
            dateRangeEnd = dateRange.selectedEndDateMillis.let {
                if (it == null) {
                    "None"
                } else {
                    it.FromHelpertoDate()
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
                            PredictWholesalesRequest_model(
                                with(ChronoUnit.DAYS){
                                    between(
                                        LocalDate.parse(dateRangeStart),
                                        LocalDate.parse(dateRangeEnd),
                                    ).toInt()
                                }
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
//                enableButton = { iswholesale() },
                modifier = Modifier
            )
        }
//        item(key = "wholesale switch") {
//            SwitchTile(
//                value = { iswholesale() },
//                onChangeValue = { onChangeiswholesale(it) },
//                title = "Group sales per day",
//                subtitle = {
//                    if (iswholesale()) "You selected to group sale by day"
//                    else "You selected to query all the Sales ungrouped"
//                },
//                modifier = Modifier
//            )
//        }
        item(key = "date input mode") {
            SwitchTile(
                value = { isDateInputMode },
                onChangeValue = {
                    isDateInputMode = it
                    when (it) {
                        true -> {
                            dateRange.displayMode = DisplayMode.Input
                        }

                        false -> {
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
                    text = "Starting Date : ${dateRangeStart}\n" +
                            "Ending Date : ${dateRangeEnd}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        }
        item(key = "calendar") {
            Card() {
                DateRangePicker(
                    state = dateRange,
                    modifier = Modifier
                        .then(
                            if (isDateInputMode) {
                                Modifier.aspectRatio(3f / 2)
                            } else {
                                Modifier.height(height = 600.dp)
                            }
                        )
                        .padding(12.dp)
                )
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
    viewmodel: OASISViewmodel,
    modifier: Modifier
) {
    val salesState by viewmodel.salesState.collectAsState()
    val config = LocalConfiguration.current

    Surface(
        modifier = modifier
    ) {
        if (salesState.listPredictedWholeSalesCache == null) {
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
                            text = "Making a query?",
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "Tap Filter on the top to make predictions",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        } else {
            Surface {
                // make a prediction layout of results rows and how to graph the past and new line
            }
        }
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
    val salesState by viewmodel.salesState.collectAsState()
    var sortby by remember { mutableStateOf<SalesResponse_model_sort>(SalesResponse_model_sort.DATE) }
    var orderByASC by remember { mutableStateOf(true) }
    val listOfSales by remember(iswholesale()) {
        derivedStateOf {
            if (iswholesale()) {
                println("whosales")
                salesState.listSalesWholesaleCache
            } else {
                salesState.listSalesCache
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
                            text = "Making a query?",
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "Tap Filter on the top",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
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
                    modifier = Modifier
                        .requiredWidth(width / 2)
                        .padding(start = 8.dp)
                )
                for (i in 1..data.size - 2) {
                    Text(
                        text = "${data[i]}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .requiredWidth(width / 2)
                            .padding(start = 8.dp)
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
            } else {
                Text(
                    text = "${data.first()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .requiredWidth(width / 2)
                        .padding(start = 8.dp)
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