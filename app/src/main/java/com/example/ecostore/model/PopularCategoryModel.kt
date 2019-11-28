package com.example.ecostore.model

class PopularCategoryModel {
    var productId:String?=null
    var menuId:String?=null
    var name:String?=null
    var image:String?=null

    constructor()

    constructor(product_id: String?, menu_id: String?, name:String?, image: String?){
        this.productId = product_id
        this.menuId = menu_id
        this.name = name
        this.image = image
    }
}