package com.ayakashikitsune.oasis.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.jsonModels.LoggerError
import com.ayakashikitsune.oasis.model.OASISViewmodel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ErrorScreen(
    viewmodel: OASISViewmodel,
    modifier: Modifier
) {
    val listerrors = viewmodel.errorLogs.collectAsState()
    Surface(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            val header = listOf("time", "message", "Function name")
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    header.forEach {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                    }
                }
            }
            items(listerrors.value.size, key = { it }) {
                ErrorLogs(loggerError = listerrors.value[it], modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ErrorLogs(
    loggerError: LoggerError,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "Name: ${loggerError.time}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Function : ${loggerError.fromFunction}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Message : ${loggerError.message}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

    }
}