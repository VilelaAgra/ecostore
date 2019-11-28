package com.example.ecostore.ui.productlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecostore.common.Common
import com.example.ecostore.model.ProductModel

class ProductListViewModel : ViewModel() {

private var mutableProductModelListData : MutableLiveData<List<ProductModel>>? = null


        fun getMutableProductModelListData():MutableLiveData<List<ProductModel>>{
            if (mutableProductModelListData == null)
                mutableProductModelListData = MutableLiveData()
            mutableProductModelListData!!.value = Common.categorySelected!!.products
            return mutableProductModelListData!!
        }
}