package com.ayakashikitsune.oasis.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayakashikitsune.oasis.data.constants.SalesResponse_model_sort
import com.ayakashikitsune.oasis.data.jsonModels.LoggerError
import com.ayakashikitsune.oasis.data.jsonModels.Query_model
import com.ayakashikitsune.oasis.model.restClient.InventoryRestClient
import com.ayakashikitsune.oasis.model.restClient.SalesRestClient
import com.ayakashikitsune.oasis.model.restClient.SetupRestClient
import com.ayakashikitsune.oasis.utils.converters.FromHelpertoDate
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
//            host = "http://192.168.84.255"
//            host = "http://192.168.1.43"
            host = "http://192.168.1.12"
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
                                            date = it.date.FromHelpertoDate()
                                        )
                                    },
                                    seven_days_wholesales = result.seven_days_wholesales.map {
                                        it.copy(
                                            date = it.date.FromHelpertoDate()
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

    fun sort(
        iswholesale: Boolean,
        sortby: SalesResponse_model_sort,
        orderByASC: Boolean
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (iswholesale) {
                    true -> {
                        _salesState.update {
                            it.copy(
                                listSalesWholesaleCache = it.listSalesWholesaleCache.run {
                                    when (sortby) {
                                        SalesResponse_model_sort.DATE -> {
                                            sortedBy {
                                                it.date
                                            }
                                        }

                                        SalesResponse_model_sort.SALE -> {
                                            sortedBy {
                                                it.sum
                                            }
                                        }

                                        else -> {
                                            sortedBy {
                                                it.date
                                            }
                                        }
                                    }

                                }.run {
                                    when (orderByASC) {
                                        true -> this
                                        false -> reversed()
                                    }
                                }
                            )
                        }
                    }

                    false -> {
                        _salesState.update {
                            it.copy(
                                listSalesCache = it.listSalesCache.run {
                                    when (sortby) {
                                        SalesResponse_model_sort.CATEGORY -> {
                                            sortedBy {
                                                it.category
                                            }
                                        }

                                        SalesResponse_model_sort.DATE -> {
                                            sortedBy {
                                                it.category
                                            }
                                        }

                                        SalesResponse_model_sort.ID -> {
                                            sortedBy {
                                                it.id
                                            }
                                        }

                                        SalesResponse_model_sort.NAME -> {
                                            sortedBy {
                                                it.name
                                            }
                                        }

                                        SalesResponse_model_sort.PRICE -> {
                                            sortedBy {
                                                it.price
                                            }
                                        }

                                        SalesResponse_model_sort.SALE -> {
                                            sortedBy {
                                                it.sale
                                            }
                                        }
                                    }
                                }.run {
                                    when (orderByASC) {
                                        true -> this
                                        false -> reversed()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    fun evaluateSalesQuery(
        queryModel: Query_model,
        onError: (String) -> Unit
    ) {
        when (queryModel.justRecentSales) {
            true -> {
                get_recent_sales(
                    iswholesale = queryModel.iswholesale,
                    onError = { onError(it) }
                )
            }

            false -> {
                when (queryModel.isMultipleDate) {
                    true -> {
                        // multiple date
                        get_sales_between(
                            fromdate = queryModel.dateRangeStart,
                            todate = queryModel.dateRangeEnd,
                            iswholesale = queryModel.iswholesale,
                            onError = { onError(it) }
                        )
                    }

                    false -> {
                        // single date
                        get_sales(
                            YYMMDD = queryModel.datePicked,
                            iswholesale = queryModel.iswholesale,
                            onError = { onError(it) }
                        )
                    }
                }
            }
        }

    }

    fun get_sales_between(
        fromdate: String,
        todate: String,
        iswholesale: Boolean,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (iswholesale) {
                    true -> {
                        try {
                            val result =
                                salesRestClient.get_sales_betweenWholesale(fromdate, todate).apply {
                                    map {
                                        it.copy(
                                            date = it.date.FromHelpertoDate()
                                        )
                                    }
                                }
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
                                            fromFunction = "get_sales_betweenWholesale"
                                        )
                                    )
                                }
                                list
                            }
                            onError(e.message ?: "error")
                        }
                    }

                    false -> {
                        try {
                            val result = salesRestClient.get_sales_between(fromdate, todate)
                            _salesState.update {
                                it.copy(
                                    listSalesCache = result
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
    }

    fun get_sales(
        YYMMDD: String,
        iswholesale: Boolean,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (iswholesale) {
                    true -> {
                        try {
                            val result = salesRestClient.get_salesWholesale(YYMMDD).apply {
                                map {
                                    it.copy(
                                        date = it.date.FromHelpertoDate()
                                    )
                                }
                            }
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

                    false -> {
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
                                    add(
                                        LoggerError(
                                            message = e.message,
                                            fromFunction = "get_sales"
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


    fun get_recent_sales(
        iswholesale: Boolean,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (iswholesale) {
                    true -> {
                        try {
                            val result = salesRestClient.get_recent_salesWholesale().apply {
                                map {
                                    it.copy(
                                        date = it.date.FromHelpertoDate()
                                    )
                                }
                            }
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
                                            fromFunction = "get_recent_sales"
                                        )
                                    )
                                }
                                list
                            }
                            onError(e.message ?: "error")
                        }
                    }

                    false -> {
                        try {
                            val result = salesRestClient.get_recent_sales().apply {
                                map {
                                    it.copy(
                                        date = it.date.FromHelpertoDate()
                                    )
                                }
                            }
                            _salesState.update {
                                it.copy(
                                    listSalesCache = result
                                )
                            }
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
    }

    fun get_recent_date(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = salesRestClient.recentDate()
                    _salesState.update {
                        it.copy(
                            max_date = result.max_date,
                            min_date = result.min_date
                        )
                    }
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

    fun predict_wholesales(
        duration: Int,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            println(duration)
            withContext(Dispatchers.IO) {
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