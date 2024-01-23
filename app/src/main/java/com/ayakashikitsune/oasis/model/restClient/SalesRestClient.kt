package com.ayakashikitsune.oasis.model.restClient

import com.ayakashikitsune.oasis.data.jsonModels.PredictWholesalesRequest_model
import com.ayakashikitsune.oasis.data.jsonModels.PredictionWholesalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
import com.ayakashikitsune.oasis.model.RestMethods
import com.ayakashikitsune.oasis.model.ServerConfig
import com.ayakashikitsune.oasis.model.ServerRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SalesRestClient(
    private val client: HttpClient,
    serverConfig: ServerConfig
) {
    var BASE_URL: String
    init{
        BASE_URL =serverConfig.getUrl()
    }

    val sales_url = "/sales"
    val get_sales                   = ServerRoutes(routeEndpoint = "${BASE_URL}$sales_url/get_sales", method = RestMethods.GET)
    val get_sales_between           = ServerRoutes(routeEndpoint = "${BASE_URL}$sales_url/get_sales", method = RestMethods.GET)
    val predict_wholesales          = ServerRoutes(routeEndpoint = "${BASE_URL}$sales_url/predict_wholesales", method = RestMethods.GET)
    val predict_sales_of_product    = ServerRoutes(routeEndpoint = "${BASE_URL}$sales_url/predict_wholesales", method = RestMethods.GET)
    val get_recent_sales            = ServerRoutes(routeEndpoint = "${BASE_URL}$sales_url/get_recent_sales", method = RestMethods.GET)

    suspend fun get_sales(date: String): List<SalesResponse_model> {
        val req = client.get(get_sales.routeEndpoint.plus(date))
        return req.body()
    }

    suspend fun get_salesWholesale(date: String): List<SalesWholesaleResponse_model> {
        val req = client.get(
            get_sales.routeEndpoint.plus(date).plus("?wholesale=true")
        )
        return req.body()
    }


    suspend fun get_sales_between(fromdate: String, todate: String): List<SalesResponse_model> {
        return client.get(get_sales_between.routeEndpoint.plus("$fromdate/$todate")).body()
    }

    suspend fun get_sales_betweenWholesale(
        fromdate: String,
        todate: String
    ): List<SalesWholesaleResponse_model> {
        return client.get(get_sales_between.routeEndpoint.plus("$fromdate/$todate").plus("?wholesale=true")).body()
    }

    suspend fun get_recent_sales() : List<SalesResponse_model> {
        return client.get(get_recent_sales.routeEndpoint).body()
    }

    suspend fun get_recent_salesWholesale() : List<SalesWholesaleResponse_model> {
        return client.get(get_recent_sales.routeEndpoint.plus("?wholesale=true")).body()
    }


    suspend fun predict_wholesales(duration: Int): List<PredictionWholesalesResponse_model> {
        return client.get(predict_wholesales.routeEndpoint.plus(duration)) {
            contentType(ContentType.Application.Json)
            setBody(PredictWholesalesRequest_model(duration))
        }.body()
    }

    suspend fun predict_sales_of_product(product: Int) {
        return client.get(predict_sales_of_product.routeEndpoint.plus(product)).body()
    }
}