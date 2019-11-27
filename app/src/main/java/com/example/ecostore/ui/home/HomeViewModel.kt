package com.example.ecostore.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecostore.callback.BestDealLoadCallback
import com.example.ecostore.callback.PopularLoadCallback
import com.example.ecostore.common.Common
import com.example.ecostore.model.BestDealModel
import com.example.ecostore.model.PopularCategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel(), PopularLoadCallback, BestDealLoadCallback {
    private lateinit var messageError: MutableLiveData<String>

    private var popularListMutableLiveData: MutableLiveData<List<PopularCategoryModel>>? = null
    private var bestDealListMutableLiveData: MutableLiveData<List<BestDealModel>>? = null

    private var popularLoadCallbackListener: PopularLoadCallback
    private var bestDealCallbackListener: BestDealLoadCallback

    val popularList: LiveData<List<PopularCategoryModel>>
        get() {
            if (popularListMutableLiveData == null) {
                popularListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadPopularList()
            }
            return popularListMutableLiveData!!
        }

    val bestDealList: LiveData<List<BestDealModel>>
        get() {
            if (bestDealListMutableLiveData == null) {
                bestDealListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadBestDealList()
            }
            return bestDealListMutableLiveData!!
        }

    private fun loadBestDealList() {
        val tempList = ArrayList<BestDealModel>()
        val bestRef = FirebaseDatabase.getInstance().getReference(Common.BEST_REF)
        bestRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(dbError: DatabaseError) {
                bestDealCallbackListener.onBestDealLoadFailed(dbError.message)
            }

            override fun onDataChange(dbSnapshot: DataSnapshot) {
                for (itemSnapShot in dbSnapshot.children) {
                    val model = itemSnapShot.getValue<BestDealModel>(BestDealModel::class.java)
                    model?.let { tempList.add(it) }
                }
                bestDealCallbackListener.onBestDealLoadSuccess(tempList)
            }
        })
    }

    private fun loadPopularList() {
        val tempList = ArrayList<PopularCategoryModel>()
        val popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_REF)
        popularRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(dbError: DatabaseError) {
                popularLoadCallbackListener.onPopularLoadFailed(dbError.message)
            }

            override fun onDataChange(dbSnapshot: DataSnapshot) {
                for (itemSnapShot in dbSnapshot.children) {
                    val model =
                        itemSnapShot.getValue<PopularCategoryModel>(PopularCategoryModel::class.java)
                    model?.let { tempList.add(it) }
                }

                popularLoadCallbackListener.onPopularLoadSuccess(tempList)
            }
        })
    }

    override fun onPopularLoadSuccess(popularModelList: List<PopularCategoryModel>) {
        popularListMutableLiveData?.value = popularModelList
    }

    override fun onPopularLoadFailed(message: String) {
        messageError.value = message
    }

    override fun onBestDealLoadSuccess(bestDealList: List<BestDealModel>) {
        bestDealListMutableLiveData?.value = bestDealList
    }

    override fun onBestDealLoadFailed(message: String) {
        messageError.value = message
    }

    init {
        popularLoadCallbackListener = this
        bestDealCallbackListener = this
    }


}