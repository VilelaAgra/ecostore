package com.example.ecostore.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CartDAO {
    @Query("SELECT * FROM Cart WHERE uid =:uid")
    fun getAllCart(uid:String) : Flowable<List<CartItem>>

    @Query("SELECT SUM(productQuantity) FROM Cart WHERE uid =:uid")
    fun countItemInCart(uid:String) : Single<Int>

    @Query("SELECT SUM(productQuantity+productPrice)*(productQuantity) FROM Cart WHERE uid =:uid")
    fun sumPrice(uid:String) : Single<Double>

    @Query("SELECT * FROM Cart WHERE productId=:productId AND uid=:uid")
    fun getItemInCart(productId:String, uid:String) : Single<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceAll(vararg cartItem: CartItem): Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCart(cart:CartItem):Single<Int>

    @Delete
    fun deleteCart(cart: CartItem):Single<Int>

    @Query("DELETE FROM Cart WHERE uid=:uid")
    fun cleanCart(uid:String) : Single<Int>

    @Query("SELECT * FROM Cart WHERE uid =:uid AND productId=:productId ")
    fun getItemWithAllOptionsInCart(uid:String, productId: String) : Single<CartItem>




}