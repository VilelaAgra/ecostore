package com.example.ecostore.ui.ad

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecostore.R
import com.example.ecostore.adapter.MyCartAdapter
import com.example.ecostore.common.Common
import com.example.ecostore.database.CartDataBase
import com.example.ecostore.database.CartDataSource
import com.example.ecostore.database.LocalCartDataSource
import com.example.ecostore.eventbus.HideFABCart
import com.example.ecostore.eventbus.UpdateItemInCart
import com.example.ecostore.ui.cart.CartViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder


class CartFragment : Fragment() {

    private var cartDataSource: CartDataSource? = null
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var recyclerViewState: Parcelable? = null
    private lateinit var cartViewModel: CartViewModel

    var textEmptyCart: TextView? = null
    var textTotalPrice: TextView? = null
    var groupPlacHolder: CardView? = null
    var recyclerCard: RecyclerView? = null

    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        EventBus.getDefault().postSticky(HideFABCart(true))

        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)

        cartViewModel.initCartDataSource(context!!)

        val root = inflater.inflate(R.layout.fragment_cart, container, false)

        initViews(root)
        cartViewModel.getMutableLiveDataCartItem().observe(this, Observer {
            if (it == null || it.isEmpty()) {
                recyclerCard?.visibility = View.GONE
                groupPlacHolder?.visibility = View.GONE
                textEmptyCart?.visibility = View.VISIBLE
            } else {
                recyclerCard?.visibility = View.VISIBLE
                groupPlacHolder?.visibility = View.VISIBLE
                textEmptyCart?.visibility = View.GONE

                val adapter = MyCartAdapter(context!!, it)
                recyclerCard?.adapter = adapter

            }
        })

        return root
    }


    private fun initViews(root: View?) {

        cartDataSource = LocalCartDataSource(CartDataBase.getInstance(context!!).cartDAO())

        recyclerCard = root!!.findViewById(R.id.recycler_cart) as RecyclerView
        recyclerCard!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerCard!!.layoutManager = layoutManager
        recyclerCard!!.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        textEmptyCart = root.findViewById(R.id.text_view_empty_cart)
        textTotalPrice = root.findViewById(R.id.cart_text_product_price)

        groupPlacHolder = root.findViewById(R.id.group_place_holder) as CardView


    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        cartViewModel?.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event: UpdateItemInCart) {
        if (event.cartItem != null) {
            recyclerViewState = recyclerCard!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(t: Int) {

                        calculateTotalPrice()
                        recyclerCard!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)

                    }

                    override fun onSubscribe(d: Disposable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, "[UPDATE CART]"+e.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }
    }

    private fun calculateTotalPrice() {

        cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Double>{
                override fun onSuccess(price: Double) {
                    textTotalPrice?.text = "500"
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "[UPDATE CART]"+e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

}