package com.example.ecostore.common

import com.example.ecostore.model.CategoryModel
import com.example.ecostore.model.ProductModel
import com.example.ecostore.model.UserModel

object Common {

    val COMMENT_REF: String              = "Comments"
    val CATEGORY_REF: String             = "Category"
    val FULL_WIDTH_COLUMN: Int           = 1
    val DEFAULT_COLUMN_COUNT: Int        = 0
    val BEST_REF: String                 = "BestDeals"
    val POPULAR_REF: String              = "MostPopular"
    val USER_REFERENCE                   = "Users"

    var currentUser: UserModel?          = null
    var categorySelected: CategoryModel? = null
    var productSelected: ProductModel?   = null

}