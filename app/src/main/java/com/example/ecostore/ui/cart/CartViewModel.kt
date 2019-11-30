package com.example.ecostore.ui.cart

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecostore.common.Common
import com.example.ecostore.database.CartDataBase
import com.example.ecostore.database.CartDataSource
import com.example.ecostore.database.CartItem
import com.example.ecostore.database.LocalCartDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CartViewModel : ViewModel() {

    private val compositeDisposable: CompositeDisposable
    private var cartDataSource: CartDataSource?=null
    private var mutableLiveDataItem: MutableLiveData<List<CartItem>>? = null

    init {
        compositeDisposable = CompositeDisposable()
    }

    fun initCartDataSource(context: Context){
        cartDataSource = LocalCartDataSource(CartDataBase.getInstance(context).cartDAO())
    }

    fun getMutableLiveDataCartItem():MutableLiveData<List<CartItem>>{
        if (mutableLiveDataItem == null)
            mutableLiveDataItem = MutableLiveData()
        getCartItems()
        return mutableLiveDataItem!!
    }

    private fun getCartItems() {
            compositeDisposable.addAll(
                cartDataSource!!.getAllCart(Common.currentUser!!.uid!!)
                .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({cartItens ->
                        mutableLiveDataItem?.value = cartItens
                    }, {
                       mutableLiveDataItem?.value = null
                    }))
        }

    fun onStop(){
        compositeDisposable.clear()
    }
}