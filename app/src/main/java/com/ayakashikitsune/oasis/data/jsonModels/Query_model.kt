package com.ayakashikitsune.oasis.data.jsonModels

data class Query_model(
    val isMultipleDate: Boolean,
    val iswholesale: Boolean,
    val justRecentSales : Boolean,
    val datePicked: String,
    val dateRangeStart: String,
    val dateRangeEnd: String
)