package com.example.ecostore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecostore.R
import com.example.ecostore.callback.RecyclerItemClickListener
import com.example.ecostore.common.Common
import com.example.ecostore.database.CartDataBase
import com.example.ecostore.database.CartDataSource
import com.example.ecostore.database.CartItem
import com.example.ecostore.database.LocalCartDataSource
import com.example.ecostore.eventbus.CountCartEvent
import com.example.ecostore.eventbus.ProductItemClick
import com.example.ecostore.model.ProductModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class MyProductListAdapter(
    internal var context: Context,
    internal var productList: List<ProductModel>
) : RecyclerView.Adapter<MyProductListAdapter.MyViewHolder>() {

    private val compositeDisposable: CompositeDisposable
    private val cartDataSource: CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDataBase.getInstance(context).cartDAO())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyProductListAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_product_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.productImage?.let {
            Glide
                .with(context)
                .load(productList[position].image)
                .into(it)
        }
        holder.productName?.text = productList[position].name
        holder.productPrice?.text = productList[position].price.toString()

        //EventBus
        holder.setListener(object : RecyclerItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Common.productSelected = productList[position]
                Common.productSelected?.key = position.toString()
                EventBus.getDefault().postSticky(ProductItemClick(true, productList[position]))
            }
        })

        holder.productImageCart?.setOnClickListener {
            val cartItem = CartItem()

            cartItem.uid = Common.currentUser?.uid
            cartItem.userPhone = Common.currentUser?.phone
            cartItem.productId = productList[position].id!!
            cartItem.productName = productList[position].name
            cartItem.productImage = productList[position].image
            cartItem.productPrice = productList[position].price.toDouble()
            cartItem.productQuantity = 1
            cartItem.productShipping = 0.0

            cartDataSource.getItemWithAllOptionsInCart(
                Common.currentUser?.uid!!,
                cartItem.productId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<CartItem> {
                    override fun onSuccess(cartItemFromDB: CartItem) {
                        if (cartItemFromDB.equals(cartItem)){
                            cartItemFromDB.productImage = cartItem.productImage
                            cartItemFromDB.productPrice = cartItem.productPrice
                            cartItemFromDB.productName = cartItem.productName
                            cartItemFromDB.productQuantity = cartItem.productQuantity + cartItem.productQuantity

                            cartDataSource.updateCart(cartItemFromDB)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : SingleObserver<Int>{
                                    override fun onSuccess(t: Int) {
                                        Toast.makeText(context, "Carrinho atualizado", Toast.LENGTH_SHORT).show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }

                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(context, "[UPDATE CART]"+e.message, Toast.LENGTH_SHORT).show()

                                    }

                                })
                        } else {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(
                                        context,
                                        "Adicionado ao Carrinho",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                }, {
                                    Toast.makeText(
                                        context,
                                        "[INSERT CART]" + it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            )
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        if (e.message!!.contains("empty")) {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(
                                        context,
                                        "Adicionado ao Carrinho",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                }, {
                                    Toast.makeText(
                                        context,
                                        "[INSERT CART]" + it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                })
                            )
                        } else {
                            Toast.makeText(context, "[CART ERRO]" + e.message, Toast.LENGTH_SHORT)
                                .show()

                        }
                    }

                })
        }
    }

    fun onStop() {
        if (compositeDisposable != null)
            compositeDisposable.clear()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var productName: TextView? = null
        var productPrice: TextView? = null
        var productImage: ImageView? = null
        var productImageFav: ImageView? = null
        var productImageCart: ImageView? = null

        internal var listener: RecyclerItemClickListener? = null

        fun setListener(listener: RecyclerItemClickListener) {
            this.listener = listener
        }

        init {
            productName = itemView.findViewById(R.id.product_list_text_view_name) as TextView
            productPrice = itemView.findViewById(R.id.product_list_text_view_price) as TextView
            productImage = itemView.findViewById(R.id.product_list_image_view) as ImageView
            productImageFav = itemView.findViewById(R.id.product_list_image_view_fav) as ImageView
            productImageCart = itemView.findViewById(R.id.product_list_image_view_cart) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            view?.let { listener?.onItemClick(it, adapterPosition) }
        }


    }


}