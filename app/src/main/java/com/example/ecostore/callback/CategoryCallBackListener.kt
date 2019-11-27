package com.example.ecostore.callback

import com.example.ecostore.model.CategoryModel

interface CategoryCallBackListener {
    fun onCategoryLoadSuccess(categoryModelList:List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}