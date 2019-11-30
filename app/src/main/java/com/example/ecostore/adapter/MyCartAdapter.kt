package com.example.ecostore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.ecostore.R
import com.example.ecostore.database.CartDataBase
import com.example.ecostore.database.CartDataSource
import com.example.ecostore.database.CartItem
import com.example.ecostore.database.LocalCartDataSource
import com.example.ecostore.eventbus.UpdateItemInCart
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class MyCartAdapter(
    internal var context: Context,
    internal var cartItem: List<CartItem>
) : RecyclerView.Adapter<MyCartAdapter.MyViewHolder>() {

    internal var compositeDisposable: CompositeDisposable
    internal var cartDataSource: CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDataBase.getInstance(context).cartDAO())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_cart_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return cartItem.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItem[position].productImage).into(holder.imageCartItem)
        holder.textProduct.text = StringBuilder(cartItem[position].productName!!)
        holder.textProductPrice.text = StringBuilder("").append(cartItem[position].productPrice!!)
        holder.numberButton.number = cartItem[position].productQuantity.toString()

        //EventBus
        holder.numberButton.setOnValueChangeListener { view, oldValue, newValue ->
            cartItem[position].productQuantity = newValue
            EventBus.getDefault().postSticky(UpdateItemInCart(cartItem[position]))
        }
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var imageCartItem: ImageView
        lateinit var textProduct: TextView
        lateinit var textProductPrice: TextView
        lateinit var numberButton: ElegantNumberButton

        init {
            imageCartItem = itemView.findViewById(R.id.image_cart) as ImageView
            textProduct = itemView.findViewById(R.id.cart_text_product_name) as TextView
            textProductPrice = itemView.findViewById(R.id.cart_text_product_price) as TextView
            numberButton = itemView.findViewById(R.id.cart_number_button) as ElegantNumberButton
        }


    }
}