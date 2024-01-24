package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class SoldCount(
    val name: String,
    val sold: Int
)