package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class SalesWholesaleResponse_model(
    val date: String,
    val sum: Double
)