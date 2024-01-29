package com.ayakashikitsune.oasis.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.dimensions.MutableDimensions
import com.patrykandpatrick.vico.core.entry.composed.ComposedChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.legend.LegendItem
import com.patrykandpatrick.vico.core.legend.VerticalLegend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.ayakashikitsune.oasis.R as localresource

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Overview_Screen(
    viewmodel: OASISViewmodel,
    paddingValues: PaddingValues
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberLazyListState()
    val status by remember(scrollState) {
        derivedStateOf {
            scrollState.firstVisibleItemIndex >= 4
        }
    }

    val overviewState by viewmodel.overviewState.collectAsState()


    val fourteen_days_wholesales by remember {
        derivedStateOf {
            overviewState.overviewresponseCache?.fourteen_days_wholesales
        }
    }
    val seven_days_wholesales by remember {
        derivedStateOf {
            overviewState.overviewresponseCache?.seven_days_wholesales
        }
    }
    val sold_count_product by remember {
        derivedStateOf {
            overviewState.overviewresponseCache?.sold_count_product
        }
    }
    val total_sales_year by remember {
        derivedStateOf {
            overviewState.overviewresponseCache?.total_sales_year ?: 0
        }
    }
    val total_sold_year by remember {
        derivedStateOf {
            overviewState.overviewresponseCache?.total_sold_year ?: 0
        }
    }

    var dialogTitle by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        dialogTitle = "getting recent date on your sales table"
        isLoading = true
        delay(1500)
        withContext(Dispatchers.IO){
            viewmodel.get_recent_date(
                onError = {
                    this.launch {
                        snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
                    }
                }
            )
        }
        dialogTitle = "gotcha!!!, your recent date is here"
        delay(1000)
        dialogTitle = "fetching your Overview details..."
        delay(1000)
        withContext(Dispatchers.IO){
            viewmodel.get_overview(
                onError = {
                    this.launch {
                        snackbarHostState.showSnackbar(
                            message = it,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
        while (overviewState.overviewresponseCache == null){
            dialogTitle = "your Overview details are here"
            delay(500)
        }
        isLoading = false
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                state = scrollState
            ) {
                stickyHeader(key = "title") {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(localresource.string.overview_title),
                                style = if (!status) {
                                    MaterialTheme.typography.displayLarge
                                } else {
                                    MaterialTheme.typography.displaySmall
                                },
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        },
                    )
                }
                item(key = "stats") {
                    CardWithTitle(
                        title = "7 days Sales",
                        modifier = Modifier
                    ) {
                        if (seven_days_wholesales != null) {
                            PlotDate(
                                x = seven_days_wholesales!!.map { it.date },
                                y = seven_days_wholesales!!.map { it.sum },
                                modifier = Modifier
                                    .aspectRatio(3f / 2)
                            )
                        } else {
                            Box(
                                modifier = Modifier.aspectRatio(3f / 2),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Empty sales")
                            }
                        }
                    }
                }
                item(key = "sold-sales") {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CardWithTitle(
                            title = "Total sales",
                            modifier = Modifier.weight(1f),
                            notify = true,
                            notifyIcon = Icons.Rounded.AttachMoney,
                            notifyColor = Color(45, 216, 129)
                        ) {
                            Text(
                                text = total_sales_year.toString(),
                                style = MaterialTheme.typography.displayMedium
                            )
                        }

                        CardWithTitle(
                            title = "Total sold",
                            modifier = Modifier.weight(1f),
                            notify = true,
                            notifyIcon = Icons.Rounded.PointOfSale,
                        ) {
                            Text(
                                text = total_sold_year.toString(),
                                style = MaterialTheme.typography.displayMedium,
                            )
                        }
                    }
                }
                if (fourteen_days_wholesales == null) {
                    item {
                        CardWithTitle(
                            title = "2 weeks sales comparison",
                            modifier = Modifier
                        ) {
                            Box(
                                modifier = Modifier.aspectRatio(3f / 2),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Empty sales")
                            }
                        }
                    }
                } else {
                    item(key = "weekly sales") {
                        CardWithTitle(
                            title = "2 weeks sales comparison",
                            modifier = Modifier
                        ) {
                            CompareTwoPlotDate(
                                fourteen_days_wholesales = fourteen_days_wholesales!!,
                                modifier = Modifier.aspectRatio(3f / 2)
                            )
                        }
                    }
                }

                if (sold_count_product == null) {
                    item {
                        Box(
                            modifier = Modifier.aspectRatio(3f / 2),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Empty Sold ranks")
                        }
                    }
                } else {
                    stickyHeader(key = "sold count header") {
                        val header = listOf("Rank", "Name", "Sold count")
                        Surface {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                header.forEachIndexed { index, text ->
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .then(
                                                if (index == 1) {
                                                    Modifier.weight(1f)
                                                } else {
                                                    Modifier
                                                }
                                            )
                                    )
                                }
                            }
                        }
                    }
                    items(sold_count_product!!.size, key = { sold_count_product!![it].name }) {
                        SoldCountRank(
                            rankNumber = it + 1,
                            name = sold_count_product!![it].name,
                            value = sold_count_product!![it].sold,
                            width = 40.dp
                        )
                    }
                }
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
}

@Composable
fun SoldCountRank(
    rankNumber: Int,
    name: String,
    value: Number,
    width: Dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
                    .size(width),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rankNumber",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Card(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "$value",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun CardWithTitle(
    title: String,
    notify: Boolean = false,
    modifier: Modifier,
    notifyIcon: ImageVector = Icons.Rounded.WarningAmber,
    notifyColor: Color = MaterialTheme.colorScheme.error,
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition()
    val offset = transition.animateFloat(
        label = "moving up down",
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    Card(
        modifier = modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedCard {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                if (notify) {
                    Icon(
                        imageVector = notifyIcon,
                        tint = notifyColor,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .graphicsLayer {
                                this.translationY = offset.value
                                this.scaleX = 1.3f
                                this.scaleY = 1.3f
                            }

                    )
                }
            }
            content()
        }
    }
}


@Composable
fun PlotDate(x: List<String>, y: List<Double>, modifier: Modifier) {
    val config = LocalConfiguration.current

    val chartEntryModel = entryModelOf(entriesOf(yValues = y.toTypedArray()))
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ -> x[value.toInt()] }

    Chart(
        chart = if (x.size > 1) lineChart() else columnChart(),
        model = chartEntryModel,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter,
            tick = LineComponent(
                margins = MutableDimensions(
                    horizontalDp = (config.screenWidthDp / 3).toFloat(),
                    verticalDp = 10f
                ),
                color = Color.Black.value.toInt()
            )
        ),
        modifier = modifier
    )
}

@Composable
fun CompareTwoPlotDate(
    fourteen_days_wholesales: List<SalesWholesaleResponse_model>,
    modifier: Modifier
) {
    val x = fourteen_days_wholesales.map { it.date }.subList(0, 7)
    val y = fourteen_days_wholesales.map { it.sum }.subList(0, 7)
    val y1 = fourteen_days_wholesales.map { it.sum }
        .subList(fourteen_days_wholesales.size - 7, fourteen_days_wholesales.size)
    val chartEntryModel = ComposedChartEntryModelProducer.build {
        add(entriesOf(yValues = y.toTypedArray()))
        add(entriesOf(yValues = y1.toTypedArray()))
    }
    Log.d("size", "${y.size} ${y1.size}")
    val horizontalAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            x[value.toInt()]
        }

    val weekOneline =
        lineChart(lines = listOf(lineSpec(lineColor = MaterialTheme.colorScheme.primary)))
    val weekTwoline =
        lineChart(lines = listOf(lineSpec(lineColor = MaterialTheme.colorScheme.secondary)))
    Chart(
        chart = remember(weekOneline, weekTwoline) { weekOneline + weekTwoline },
        chartModelProducer = chartEntryModel,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter,
            sizeConstraint = Axis.SizeConstraint.TextWidth(x.max())
        ),
        legend = VerticalLegend(
            items = listOf(
                LegendItem(
                    icon = ShapeComponent(
                        color = MaterialTheme.colorScheme.primary.toArgb()
                    ),
                    labelText = "week 1",
                    label = textComponent()
                ),
                LegendItem(
                    icon = ShapeComponent(
                        color = MaterialTheme.colorScheme.secondary.toArgb()
                    ),
                    labelText = "week 2",
                    label = textComponent()
                )
            ),
            iconPaddingDp = 8.dp.value,
            iconSizeDp = 8.dp.value
        ),
        modifier = modifier
    )
}
