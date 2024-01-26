package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class RecentDateResponse_model(
    val min_date : String,
    val max_date : String
)