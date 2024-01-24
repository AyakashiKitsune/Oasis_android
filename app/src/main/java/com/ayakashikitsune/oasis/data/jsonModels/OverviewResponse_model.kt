package com.ayakashikitsune.oasis.data.jsonModels

import kotlinx.serialization.Serializable

@Serializable
data class OverviewResponse_model(
    val fourteen_days_wholesales: List<SalesWholesaleResponse_model>,
    val seven_days_wholesales: List<SalesWholesaleResponse_model>,
    val sold_count_product : List<SoldCount>,
    val total_sales_year: Int,
    val total_sold_year: Int
)