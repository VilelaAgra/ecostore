package com.example.ecostore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecostore.R
import com.example.ecostore.callback.RecyclerItemClickListener
import com.example.ecostore.common.Common
import com.example.ecostore.eventbus.ProductItemClick
import com.example.ecostore.model.ProductModel
import org.greenrobot.eventbus.EventBus

class MyProductListAdapter(
    internal var context: Context,
    internal var productList: List<ProductModel>
) : RecyclerView.Adapter<MyProductListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductListAdapter.MyViewHolder {
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
        holder.setListener(object :RecyclerItemClickListener{
            override fun onItemClick(view: View, position: Int) {
                Common.productSelected = productList[position]
                Common.productSelected?.key = position.toString()
                EventBus.getDefault().postSticky(ProductItemClick(true, productList[position] ))
            }

        })
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var productName: TextView?       = null
        var productPrice: TextView?      = null
        var productImage: ImageView?     = null
        var productImageFav: ImageView?  = null
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