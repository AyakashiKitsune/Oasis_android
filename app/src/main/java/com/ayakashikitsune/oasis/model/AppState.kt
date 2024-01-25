package com.ayakashikitsune.oasis.model

import com.ayakashikitsune.oasis.data.jsonModels.OverviewResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.PredictionWholesalesResponse_model

data class SalesState(
    val listSalesCache: List<SalesResponse_model> = emptyList(),
    val listSalesWholesaleCache: List<SalesWholesaleResponse_model> = emptyList(),

    val listPredictedWholeSalesCache: List<PredictionWholesalesResponse_model> = emptyList(),

//    val recentSalesCache : List<SalesResponse_model> = emptyList(),
//    val recentSalesWholesaleCache : List<SalesWholesaleResponse_model> = emptyList()
)

data class OverviewState(
    val overviewresponseCache : OverviewResponse_model? = null
)

