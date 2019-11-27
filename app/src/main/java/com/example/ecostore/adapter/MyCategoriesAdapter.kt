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
import com.example.ecostore.common.Common
import com.example.ecostore.model.CategoryModel

class MyCategoriesAdapter(
    internal var context: Context,
    internal var categoriesList: List<CategoryModel>
) : RecyclerView.Adapter<MyCategoriesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_category_menu_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.categoryImage?.let {
            Glide
                .with(context)
                .load(categoriesList[position].image)
                .into(it)
        }
        holder.categoryName?.text = categoriesList[position].name
    }

    override fun getItemViewType(position: Int): Int {
        return if (categoriesList.size == 1) {
            Common.DEFAULT_COLUMN_COUNT
        } else {
            if (categoriesList.size % 2 == 0) {
                Common.DEFAULT_COLUMN_COUNT
            } else {
                if (position > 1 && position == categoriesList.size-1){
                    Common.FULL_WIDTH_COLUMN
                } else {
                    Common.DEFAULT_COLUMN_COUNT
                }
            }
        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var categoryName: TextView? = null
        var categoryImage: ImageView? = null

        init {
            categoryName = itemView.findViewById(R.id.category_menu_item_text_view) as TextView
            categoryImage = itemView.findViewById(R.id.category_menu_item_image_view) as ImageView
        }
    }
}