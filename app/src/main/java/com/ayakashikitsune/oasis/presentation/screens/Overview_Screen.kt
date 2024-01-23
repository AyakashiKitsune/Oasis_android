package com.ayakashikitsune.oasis.presentation.screens

import android.icu.util.Calendar
import android.util.Log
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayakashikitsune.oasis.model.OASISViewmodel
import com.ayakashikitsune.oasis.utils.converters.digit_month
import com.ayakashikitsune.oasis.ui.theme.OASISTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.entriesOf
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.ayakashikitsune.oasis.R as localresource

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Overview_Screen(
    viewmodel :OASISViewmodel,
    paddingValues: PaddingValues
) {
    val config = LocalConfiguration.current
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberLazyListState()
    val status by remember(scrollState){ derivedStateOf {
        scrollState.firstVisibleItemIndex >= 3
    }}
    val weeklysalesrankfont = animateFloatAsState(
        targetValue = if (status) {
            MaterialTheme.typography.displayLarge.fontSize.value
        }else {
            MaterialTheme.typography.displaySmall.fontSize.value
        },
        label = "font"
    )
    LaunchedEffect(true){
        viewmodel.get_recent_sales(
            onError = {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
            }
        )
    }
    val data = listOf(
        "2022-07-01" to 2f,
        "2022-07-02" to 6f,
        "2022-07-04" to 4f
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padd->
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
                                }else {
                                    MaterialTheme.typography.displaySmall
                                },
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        },
                    )
                }
                item(key = "stats") {
                    CardWithTitle(
                        title = "Sales Statistics",
                        modifier = Modifier
                    ) {
                        plotDate(
                            x = data.map{ it.first },
                            y = data.map { it.second },
                            modifier = Modifier
                                .aspectRatio(3f / 2)
                        )
                    }
                }
                item(key = "sold") {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CardWithTitle(
                            title = "Total sales",
                            modifier = Modifier.weight(1f),
                            notify = true,
                            notifyIcon = Icons.Rounded.TrendingUp,
                            notifyColor = Color(45, 216, 129)
                        ) {
                            Text(
                                text = "23.2k",
                                style = MaterialTheme.typography.displayMedium
                            )
                        }

                        CardWithTitle(
                            title = "Total sold",
                            modifier = Modifier.weight(1f),
                            notify = true,
                            notifyIcon = Icons.Rounded.TrendingDown,
                        ) {
                            Text(
                                text = "10k",
                                style = MaterialTheme.typography.displayMedium,
                            )
                        }
                    }
                }
                stickyHeader(key = "title weekly sales") {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ){
                        Text(
                            text = "Weekly Sales Rank",
                            style = if (status) {
                                MaterialTheme.typography.displayMedium
                            } else {
                                MaterialTheme.typography.displaySmall
                            },
                            fontSize = weeklysalesrankfont.value.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                items(20, key = { it }) {
                    WeeklySalesRank(
                        rankNumber = it + 1,
                        name = "$it s",
                        width = config.screenWidthDp.dp * 0.1f,
                        value = (-20..100).random()
                    )
                }
            }
        }
    }

}

@Composable
fun WeeklySalesRank(
    rankNumber: Int,
    name: String,
    value: Number,
    width: Dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(horizontal = 16.dp)
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
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Card(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    if (value.toDouble() > 0) "+$value" else "-$value",
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
fun plotDate(x : List<String>, y : List<Float>, modifier: Modifier) {
    val data = x.zip(y).associate { (dateString, yValue) ->
        LocalDate.parse(dateString) to yValue
    }
    val xValuesToDates = data.keys.associateBy { it.toEpochDay().toFloat() }
    val chartEntryModel = entryModelOf(entriesOf(yValues = data.values.toTypedArray()))
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
    val horizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        (xValuesToDates[value] ?: LocalDate.ofEpochDay(value.toLong())).format(dateTimeFormatter)
    }

    Chart(
        chart = lineChart(),
        model = chartEntryModel,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(
            valueFormatter = horizontalAxisValueFormatter
        ),
        modifier = modifier
    )
}
