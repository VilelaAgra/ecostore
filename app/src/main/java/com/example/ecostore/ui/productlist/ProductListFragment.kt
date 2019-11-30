package com.example.ecostore.ui.productlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecostore.R
import com.example.ecostore.adapter.MyProductListAdapter
import com.example.ecostore.common.Common

class ProductListFragment : Fragment() {

    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var layoutAnimationController: LayoutAnimationController
    var adapter: MyProductListAdapter?                        = null
    var recyclerProductList: RecyclerView?                    = null


    override fun onStop() {
        if (adapter != null)
            adapter?.onStop()
        super.onStop()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        productListViewModel =
            ViewModelProviders.of(this).get(ProductListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_product_list, container, false)

        initViews(root)

        productListViewModel.getMutableProductModelListData().observe(this, Observer {
            adapter = context?.let { it1 -> MyProductListAdapter(it1, it) }
            recyclerProductList?.adapter = adapter
            recyclerProductList?.layoutAnimation = layoutAnimationController
        })
        return root
    }

    private fun initViews(root: View?) {
        recyclerProductList = root!!.findViewById(R.id.recycler_product_list) as RecyclerView
        recyclerProductList?.setHasFixedSize(true)
        recyclerProductList?.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        (activity as AppCompatActivity).supportActionBar?.title = Common.categorySelected?.name.toString()

    }
}