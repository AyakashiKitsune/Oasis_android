package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class PredictionWholesalesResponse_model(
    val buffer_lags: List<BufferLag>,
    val prediction: List<Prediction>
)