package com.ayakashikitsune.oasis.model

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayakashikitsune.oasis.data.jsonModels.LoggerError
import com.ayakashikitsune.oasis.model.restClient.InventoryRestClient
import com.ayakashikitsune.oasis.model.restClient.SalesRestClient
import com.ayakashikitsune.oasis.model.restClient.SetupRestClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.ConnectException
import java.util.Locale

class
OASISViewmodel : ViewModel() {
    val rest_client: HttpClient = HttpClient(Android) {
        engine {
            connectTimeout = 100_000
            socketTimeout = 100_000
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(Logging) {
            logger = Logger.DEFAULT

        }
    }

    private val _serverConfig = MutableStateFlow(
        ServerConfig(
            host = "http://192.168.84.255"
        )
    )
    val serverConfig = _serverConfig.asStateFlow()

    val setupRestClient = SetupRestClient(rest_client, _serverConfig.value)
    val salesRestClient = SalesRestClient(rest_client, _serverConfig.value)
    val inventoryRestClient = InventoryRestClient(rest_client, _serverConfig.value)

    private val _errorLogs = MutableStateFlow(listOf<LoggerError>())
    val errorLogs = _errorLogs.asStateFlow()


    private val _salesState = MutableStateFlow(SalesState())
    val salesState = _salesState.asStateFlow()

    private val _overviewState = MutableStateFlow(OverviewState())
    val overviewState = _overviewState.asStateFlow()

    fun get_overview(
        onError: suspend (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    if (_overviewState.value.overviewresponseCache == null) {
                        val result = salesRestClient.get_overview()
                        _overviewState.update { state ->
                            state.copy(
                                overviewresponseCache = result.copy(
                                    fourteen_days_wholesales = result.fourteen_days_wholesales.map {
                                        it.copy(
                                            date = it.date.let {

                                                val originalDate = SimpleDateFormat(
                                                    "EEE, dd MMM yyyy HH:mm:ss z",
                                                    Locale.US
                                                ).parse(it)
                                                SimpleDateFormat("EEE", Locale.US).format(
                                                    originalDate
                                                )
                                            }
                                        )
                                    },
                                    seven_days_wholesales = result.seven_days_wholesales.map {
                                        it.copy(
                                            date = it.date.let {
                                                val originalDate = SimpleDateFormat(
                                                    "EEE, dd MMM yyyy HH:mm:ss z",
                                                    Locale.US
                                                ).parse(it)
                                                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
                                                    originalDate
                                                )
                                            }
                                        )
                                    }
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    _errorLogs.update {
                        it.toMutableList().apply {
                            add(LoggerError(message = e.message, fromFunction = "get_overview"))
                        }
                    }
                    onError(e.message ?: "error")
                }
            }
        }
    }

    fun get_sales(
        YYMMDD: String,
        requery: Boolean = false,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (_salesState.value.listSalesCache.isEmpty() or requery) {
                    try {
                        val result = salesRestClient.get_sales(YYMMDD)
                        _salesState.update {
                            it.copy(
                                listSalesCache = result
                            )
                        }
                    } catch (e: Exception) {
                        _errorLogs.update {
                            val list = it.toMutableList().apply {
                                add(LoggerError(message = e.message, fromFunction = "get_sales"))
                            }
                            list
                        }
                        onError(e.message ?: "error")
                    }
                }
            }
        }
    }

    fun get_salesWholesale(
        YYMMDD: String,
        requery: Boolean = false,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (_salesState.value.listSalesWholesaleCache.isEmpty() or requery) {
                    try {
                        val result = salesRestClient.get_salesWholesale(YYMMDD)
                        _salesState.update {
                            it.copy(
                                listSalesWholesaleCache = result
                            )
                        }
                    } catch (e: Exception) {
                        _errorLogs.update {
                            val list = it.toMutableList().apply {
                                add(
                                    LoggerError(
                                        message = e.message,
                                        fromFunction = "get_salesWholesale"
                                    )
                                )
                            }
                            list
                        }
                        onError(e.message ?: "error")
                    }
                }
            }
        }
    }

    fun get_recent_sales(
        requery: Boolean = false,
        onError: suspend (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (_salesState.value.recentSalesCache.isEmpty() or requery) {
                    try {
                        val result = salesRestClient.get_recent_sales()
                        _salesState.update {
                            it.copy(
                                recentSalesCache = result
                            )
                        }
                    } catch (e: ConnectException) {
                        _errorLogs.update {
                            val list = it.toMutableList().apply {
                                add(
                                    LoggerError(
                                        message = e.message,
                                        fromFunction = "get_recent_sales"
                                    )
                                )
                            }
                            list
                        }
                        onError("connection to server failed")
                    } catch (e: Exception) {
                        _errorLogs.update {
                            val list = it.toMutableList().apply {
                                add(
                                    LoggerError(
                                        message = e.message,
                                        fromFunction = "get_recent_sales"
                                    )
                                )
                            }
                            list
                        }
                        onError(e.message ?: "error")
                    }
                }
            }
        }
    }


    fun predict_wholesales(
        duration: Int,
        requery: Boolean = false,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (_salesState.value.listPredictedWholeSalesCache.isEmpty() or requery) {
                    try {
                        val result = salesRestClient.predict_wholesales(duration)
                        _salesState.update {
                            it.copy(
                                listPredictedWholeSalesCache = result
                            )
                        }
                    } catch (e: Exception) {
                        _errorLogs.update {
                            val list = it.toMutableList().apply {
                                add(
                                    LoggerError(
                                        message = e.message,
                                        fromFunction = "predict_wholesales"
                                    )
                                )
                            }
                            list
                        }
                        onError(e.message ?: "error")
                    }

                }
            }
        }
    }


}