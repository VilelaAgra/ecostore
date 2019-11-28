package com.example.ecostore.model

class ProductModel {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var description: String? = null
    var price: Long = 0
    var key: String? = null
    var addon: List<AddonModel> = ArrayList()
    var sizemudarparaoutracoisa: List<SizeModel> = ArrayList()


    var ratingValue: Double = 0.toDouble()
    var ratingCount: Long = 0.toLong()


}
