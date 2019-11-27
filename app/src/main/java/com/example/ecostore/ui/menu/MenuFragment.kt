package com.example.ecostore.ui.menu

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecostore.R
import com.example.ecostore.adapter.MyCategoriesAdapter
import com.example.ecostore.common.Common
import com.example.ecostore.common.SpacesItemDecoration
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_menu_categories.*

class MenuFragment : Fragment() {

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapater: MyCategoriesAdapter? = null
    private var recyclerMenuCategory: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuViewModel =
            ViewModelProviders.of(this).get(MenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_menu_categories, container, false)

        initViews(root)

        menuViewModel.getMessageError()?.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        menuViewModel.getCategoryList().observe(this, Observer {
            dialog.dismiss()
            adapater = MyCategoriesAdapter(context!!, it)
            recyclerMenuCategory?.adapter = adapater
            recyclerMenuCategory?.layoutAnimation = layoutAnimationController
        })


        return root
    }

    private fun initViews(root: View) {
        dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false).build()
        dialog.show()
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerMenuCategory = root.findViewById(R.id.recycler_menu_category) as RecyclerView
        recyclerMenuCategory?.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.orientation = RecyclerView.VERTICAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapater != null) {
                    when (adapater?.getItemViewType(position)) {
                        Common.DEFAULT_COLUMN_COUNT -> 1
                        Common.FULL_WIDTH_COLUMN -> 2
                        else -> -1
                    }
                } else {
                    -1
                }
            }

        }
        recyclerMenuCategory?.layoutManager = layoutManager
        recyclerMenuCategory?.addItemDecoration(SpacesItemDecoration(8))
    }
}