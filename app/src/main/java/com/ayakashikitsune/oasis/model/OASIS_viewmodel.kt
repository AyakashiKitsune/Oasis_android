package com.ayakashikitsune.oasis.model

import androidx.lifecycle.ViewModel
import com.ayakashikitsune.oasis.data.jsonModels.LoggerError
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
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
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.ConnectException

class OASISViewmodel : ViewModel() {
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

    private val _serverConfig = MutableStateFlow(ServerConfig())
    val serverConfig = _serverConfig.asStateFlow()

    val setupRestClient     = SetupRestClient(rest_client,_serverConfig.value)
    val salesRestClient     = SalesRestClient(rest_client,_serverConfig.value)
    val inventoryRestClient = InventoryRestClient(rest_client,_serverConfig.value)

    private val _errorLogs = MutableStateFlow(listOf<LoggerError>())
    val errorLogs = _errorLogs.asStateFlow()


    private val _salesState = MutableStateFlow(SalesState())
    val salesState = _salesState.asStateFlow()

    suspend fun get_sales(
        YYMMDD: String,
        requery : Boolean = false,
        onError: (String) -> Unit
    )  {
        withContext(Dispatchers.IO) {
            if(_salesState.value.listSalesCache.isEmpty() or  requery){
                try{
                    val result = salesRestClient.get_sales(YYMMDD)
                    _salesState.update {
                        it.copy(
                            listSalesCache = result
                        )
                    }
                }catch (e :Exception){
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
    suspend fun get_salesWholesale(
        YYMMDD: String,
        requery : Boolean = false,
        onError: (String) -> Unit
    ){
        withContext(Dispatchers.IO) {
            if(_salesState.value.listSalesWholesaleCache.isEmpty() or requery){
                try {
                    val result = salesRestClient.get_salesWholesale(YYMMDD)
                    _salesState.update {
                        it.copy(
                            listSalesWholesaleCache = result
                        )
                    }
                }catch (e : Exception){
                    _errorLogs.update {
                        val list = it.toMutableList().apply {
                            add(LoggerError(message = e.message, fromFunction = "get_salesWholesale"))
                        }
                        list
                    }
                    onError(e.message ?: "error")
                }
            }
        }
    }

    suspend fun get_recent_sales(
        requery: Boolean = false,
        onError: suspend (String) -> Unit
    ){
        withContext(Dispatchers.IO){
            if(_salesState.value.recentSalesCache.isEmpty() or requery){
                try{
                    val result = salesRestClient.get_recent_sales( )
                    _salesState.update {
                        it.copy(
                            recentSalesCache = result
                        )
                    }
                }catch (e : ConnectException){
                    _errorLogs.update {
                        val list = it.toMutableList().apply {
                            add(LoggerError(message = e.message, fromFunction = "get_recent_sales"))
                        }
                        list
                    }
                    onError("connection to server failed")
                }catch (e : Exception){
                    _errorLogs.update {
                        val list = it.toMutableList().apply {
                            add(LoggerError(message = e.message, fromFunction = "get_recent_sales"))
                        }
                        list
                    }
                    onError(e.message ?: "error")
                }
            }
        }
    }


    suspend fun predict_wholesales(
        duration : Int,
        requery: Boolean = false,
        onError : (String)->Unit
    ){
        withContext(Dispatchers.IO){
            if(_salesState.value.listPredictedWholeSalesCache.isEmpty() or requery){
                try {
                    val result = salesRestClient.predict_wholesales(duration)
                    _salesState.update {
                        it.copy(
                            listPredictedWholeSalesCache = result
                        )
                    }
                }catch (e :Exception){
                    _errorLogs.update {
                        val list = it.toMutableList().apply {
                            add(LoggerError(message = e.message, fromFunction = "predict_wholesales"))
                        }
                        list
                    }
                    onError(e.message ?: "error")
                }

            }
        }
    }



}