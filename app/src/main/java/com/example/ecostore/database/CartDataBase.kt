package com.example.ecostore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [CartItem::class], exportSchema = false)
abstract class CartDataBase : RoomDatabase() {

    abstract fun cartDAO(): CartDAO

    companion object {
        private var instance: CartDataBase? = null

        fun getInstance(context: Context): CartDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder<CartDataBase>(
                    context,
                    CartDataBase::class.java!!,
                    "EcoStoreDB"
                ).build()
            }
            return instance!!
        }
    }
}