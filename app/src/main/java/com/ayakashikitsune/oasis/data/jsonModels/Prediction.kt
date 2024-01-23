package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class Prediction(
    val date: String,
    val sales: Double
)