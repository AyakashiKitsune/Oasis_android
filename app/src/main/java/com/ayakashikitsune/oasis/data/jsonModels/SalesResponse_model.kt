package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class SalesResponse_model(
    val category: String,
    val date: String,
    val id: Int,
    val name: String,
    val price: Double,
    val sale: Double
)

