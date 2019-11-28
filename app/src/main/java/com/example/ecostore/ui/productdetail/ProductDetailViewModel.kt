package com.example.ecostore.ui.productdetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecostore.common.Common
import com.example.ecostore.model.CommentModel
import com.example.ecostore.model.ProductModel

class ProductDetailViewModel : ViewModel() {

    private var mutableLiveDataProduct: MutableLiveData<ProductModel>? = null
    private var mutableLiveDataComment: MutableLiveData<CommentModel>? = null

    init {
        mutableLiveDataComment = MutableLiveData()
    }

    fun getMutableLiveDataProduct(): MutableLiveData<ProductModel>{
        if (mutableLiveDataProduct == null)
            mutableLiveDataProduct = MutableLiveData()
        mutableLiveDataProduct?.value = Common.productSelected
        return mutableLiveDataProduct!!
    }

    fun setProductModel(productModel: ProductModel) {
        if (mutableLiveDataProduct != null){
            mutableLiveDataProduct!!.value = productModel
        }
    }

    fun getMutableLiveDataComment(): MutableLiveData<CommentModel>{
        if (mutableLiveDataComment == null)
            mutableLiveDataComment = MutableLiveData()
        return mutableLiveDataComment!!
    }

    fun setCommentModel(commentModel: CommentModel) {
        if (mutableLiveDataComment != null){
            mutableLiveDataComment!!.value = (commentModel)
        }

    }




}