package com.example.ecostore.model

class PopularCategoryModel {
    var product_id:String?=null
    var menu_id:String?=null
    var name:String?=null
    var image:String?=null

    constructor()

    constructor(product_id: String?, menu_id: String?, name:String?, image: String?){
        this.product_id = product_id
        this.menu_id = menu_id
        this.name = name
        this.image = image
    }
}