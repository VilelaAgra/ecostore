package com.example.ecostore.ui.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecostore.callback.CategoryCallBackListener
import com.example.ecostore.common.Common
import com.example.ecostore.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuViewModel : ViewModel(), CategoryCallBackListener {
    private var categoriesListMutableLiveData: MutableLiveData<List<CategoryModel>>? = null
    private var messageError: MutableLiveData<String>? = MutableLiveData()

    private val categoryCallBackListener: CategoryCallBackListener

    init {
        categoryCallBackListener = this
    }

    override fun onCategoryLoadSuccess(categoryModelList: List<CategoryModel>) {
        categoriesListMutableLiveData?.value = categoryModelList
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError?.value = message
    }

    fun getCategoryList(): MutableLiveData<List<CategoryModel>> {
        if (categoriesListMutableLiveData == null) {
            categoriesListMutableLiveData = MutableLiveData()
            loadCategory()
        }
        return categoriesListMutableLiveData!!
    }

    fun getMessageError(): MutableLiveData<String>? {
        return messageError
    }

    private fun loadCategory() {
        val tempList = ArrayList<CategoryModel>()
        val categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(dbError: DatabaseError) {
                categoryCallBackListener.onCategoryLoadFailed(dbError.message)
            }

            override fun onDataChange(dbSnapshot: DataSnapshot) {
                for (itemSnapShot in dbSnapshot.children) {
                    val model = itemSnapShot.getValue<CategoryModel>(CategoryModel::class.java)
                    model?.menu_id = itemSnapShot.key
                    model?.let { tempList.add(it) }
                }
                categoryCallBackListener.onCategoryLoadSuccess(tempList)
            }
        })
    }
}

