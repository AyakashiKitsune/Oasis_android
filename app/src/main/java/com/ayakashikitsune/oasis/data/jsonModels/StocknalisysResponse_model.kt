package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class StocknalisysResponse_model(
    val april: Double,
    val august: Double,
    val average: Double,
    val december: Double,
    val february: Double,
    val id: Int,
    val january: Double,
    val july: Double,
    val june: Double,
    val march: Double,
    val max: Double,
    val may: Double,
    val min: Double,
    val name: String,
    val november: Double,
    val october: Double,
    val september: Double
)