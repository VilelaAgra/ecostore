package com.example.ecostore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecostore.model.PopularCategoryModel
import com.example.ecostore.R
import de.hdodenhof.circleimageview.CircleImageView

class MyPopularCategoriesAdapter(
    internal var context: Context,
    internal var popularCategoryModels: List<PopularCategoryModel>
) : RecyclerView.Adapter<MyPopularCategoriesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_popular_categories_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return popularCategoryModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.categoryImage?.let {
            Glide
                .with(context)
                .load(popularCategoryModels[position].image)
                .into(it)
        }
        holder.categoryName?.text = popularCategoryModels[position].name
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var categoryName: TextView? = null
        var categoryImage: CircleImageView? = null

        init {
            categoryName =  itemView.findViewById(R.id.category_name) as TextView
            categoryImage =  itemView.findViewById(R.id.category_image) as CircleImageView
        }
    }
}