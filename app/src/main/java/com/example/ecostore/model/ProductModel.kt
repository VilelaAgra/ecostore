package com.example.ecostore.model

class ProductModel {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var description: String? = null
    var price: Long = 0
    var addon: List<AddonModel> = ArrayList<AddonModel>()
    var sizemudarparaoutracoisa: List<SizeModel> = ArrayList<SizeModel>()


}
