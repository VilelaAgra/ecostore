package com.example.ecostore.callback

import com.example.ecostore.model.BestDealModel

interface BestDealLoadCallback {
    fun onBestDealLoadSuccess(bestDealList:List<BestDealModel>)
    fun onBestDealLoadFailed(message:String)
}