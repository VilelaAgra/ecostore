package com.example.ecostore.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
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


    fun setSpanString(welcome: String, name: String?, textUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(welcome)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name?.length!!, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        textUser?.setText(builder, TextView.BufferType.SPANNABLE)
    }

}