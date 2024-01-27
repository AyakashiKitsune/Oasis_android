package com.ayakashikitsune.oasis.model.restClient

import com.ayakashikitsune.oasis.data.jsonModels.SaveKillResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.StocknalisysResponse_model
import com.ayakashikitsune.oasis.model.RestMethods
import com.ayakashikitsune.oasis.model.ServerConfig
import com.ayakashikitsune.oasis.model.ServerRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class InventoryRestClient(
    private val client : HttpClient,
    serverConfig: ServerConfig

) {
    var BASE_URL: String
    init{
        BASE_URL =serverConfig.getUrl()
    }

    val inventory_url = "/inventory"
    val get_inventory = ServerRoutes("$BASE_URL$inventory_url/get_inventory", method = RestMethods.GET)
    val analyze_stocking = ServerRoutes("$BASE_URL$inventory_url/stock_analysis", method = RestMethods.GET)
    val analyze_savekill_product = ServerRoutes("$BASE_URL$inventory_url/savekill", method = RestMethods.GET)

    suspend fun get_inventory() {
        return client.get(get_inventory.routeEndpoint).body()
    }
    suspend fun analyze_savekill_product() : List<SaveKillResponse_model>{
        return client.get(analyze_savekill_product.routeEndpoint).body()
    }
    suspend fun analyze_stocking(): List<StocknalisysResponse_model> {
        return client.get(analyze_stocking.routeEndpoint).body()
    }

}
