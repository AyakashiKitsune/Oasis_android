package com.ayakashikitsune.oasis.model.restClient

import com.ayakashikitsune.oasis.model.RestMethods
import com.ayakashikitsune.oasis.model.ServerConfig
import com.ayakashikitsune.oasis.model.ServerRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post

class SetupRestClient(
    private val client : HttpClient,
    serverConfig: ServerConfig
) {
    var BASE_URL: String
    init{
        BASE_URL =serverConfig.getUrl()
    }
    val setup_url = "/setup"
    val create_Database    = ServerRoutes(routeEndpoint = "$BASE_URL$setup_url/create_Database", method =  RestMethods.POST)
    val send_existing      = ServerRoutes(routeEndpoint = "$BASE_URL$setup_url/send_existing", method =  RestMethods.POST)
    val csv_to_sql         = ServerRoutes(routeEndpoint = "$BASE_URL$setup_url/csv_to_sql", method = RestMethods.POST)
    val auto_columns       = ServerRoutes(routeEndpoint = "$BASE_URL$setup_url/auto_columns", method = RestMethods.GET)
    val ten_column_sample  = ServerRoutes(routeEndpoint = "$BASE_URL$setup_url/ten_column_sample", method = RestMethods.GET)
    val manual_column       = ServerRoutes(routeEndpoint = "$BASE_URL$setup_url/manual_column", method = RestMethods.POST)

    suspend fun create_Database(){
        return client.post(create_Database.routeEndpoint).body()
    }

    suspend fun send_existing() {
        return client.post(send_existing.routeEndpoint).body()
    }
    suspend fun csv_to_sql(){
        return client.post(csv_to_sql.routeEndpoint).body()
    }
    suspend fun auto_columns(){
        return client.get(auto_columns.routeEndpoint).body()
    }
    suspend fun ten_column_sample() {
        return client.get(ten_column_sample.routeEndpoint).body()
    }
    suspend fun manual_column() {
        return client.post(manual_column.routeEndpoint).body()
    }

}