package com.example.ecostore.callback

import android.view.View

interface RecyclerItemClickListener {
    fun onItemClick(view: View, position: Int)
}