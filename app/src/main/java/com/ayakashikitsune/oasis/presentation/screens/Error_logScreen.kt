package com.ayakashikitsune.oasis.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ayakashikitsune.oasis.data.jsonModels.LoggerError
import com.ayakashikitsune.oasis.model.OASISViewmodel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(
    viewmodel: OASISViewmodel,
    modifier: Modifier
) {
    val listerrors by viewmodel.errorLogs.collectAsState()
    Surface(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                TopAppBar(title = {
                    Text(
                        text = "Error logs",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                })
            }
            items(listerrors.size, key = { "${listerrors[it].time}$it" }) {
                ErrorLogs(loggerError = listerrors[it], modifier = Modifier.fillMaxWidth())
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
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = "Name: ${loggerError.time}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Function : ${loggerError.fromFunction}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = "Message : ${loggerError.message}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

    }
}