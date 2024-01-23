package com.ayakashikitsune.oasis.model

data class ServerRoutes(val routeEndpoint : String, val method : RestMethods)

data class ServerConfig(
    val host : String = "http://192.168.1.12",
    val port : String = "5000"
){
    fun getUrl() : String{
        return "$host:$port"
    }
}

enum class RestMethods{
    POST,
    GET
}
