package com.ayakashikitsune.oasis.model

import com.ayakashikitsune.oasis.data.jsonModels.OverviewResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesWholesaleResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.PredictionWholesalesResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.SaveKillResponse_model
import com.ayakashikitsune.oasis.data.jsonModels.StocknalisysResponse_model

data class SalesState(
    val listSalesCache: List<SalesResponse_model> = emptyList(),
    val listSalesWholesaleCache: List<SalesWholesaleResponse_model> = emptyList(),

    val listPredictedWholeSalesCache: PredictionWholesalesResponse_model? = null,

    val max_date : String = "",
    val min_date : String = ""

)

data class OverviewState(
    val overviewresponseCache : OverviewResponse_model? = null
)

data class InventoryState(
    val listofSaveKill : List<SaveKillResponse_model>? = null,
    val listofStocknalysis : List<StocknalisysResponse_model>? = null,
    val indexTab : Int = 0,

){
    fun onchangeIndex(value :Int) : InventoryState{
        return this.copy(indexTab =  value)
    }
}

