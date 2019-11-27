package com.example.ecostore.callback

import com.example.ecostore.model.PopularCategoryModel

interface PopularLoadCallback {
    fun onPopularLoadSuccess(popularModelList:List<PopularCategoryModel>)
    fun onPopularLoadFailed(message:String)

}