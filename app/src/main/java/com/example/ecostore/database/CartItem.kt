package com.example.ecostore.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart", primaryKeys = ["uid", "productId"])
class CartItem {

    @NonNull
    @ColumnInfo(name = "productId")
    var productId: String = ""

    @ColumnInfo(name = "productName")
    var productName: String? = null

    @ColumnInfo(name = "productImage")
    var productImage: String? = null

    @ColumnInfo(name = "productPrice")
    var productPrice: Double = 0.0

    @ColumnInfo(name = "productQuantity")
    var productQuantity: Int = 0

    @ColumnInfo(name = "productShipping")
    var productShipping: Double = 0.0

    @ColumnInfo(name = "userPhone")
    var userPhone: String? = ""

    @NonNull
    @ColumnInfo(name = "uid")
    var uid: String? = ""

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is CartItem) return false

        val cartItem = other as CartItem?

        return cartItem?.productId == this.productId
    }
}