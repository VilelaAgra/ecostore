package com.example.ecostore

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecostore.common.Common
import com.example.ecostore.database.CartDataBase
import com.example.ecostore.database.CartDataSource
import com.example.ecostore.database.LocalCartDataSource
import com.example.ecostore.eventbus.CategoryClick
import com.example.ecostore.eventbus.CountCartEvent
import com.example.ecostore.eventbus.HideFABCart
import com.example.ecostore.eventbus.ProductItemClick
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var cartDataSource: CartDataSource
    private lateinit var navController: NavController
    private lateinit var drawer: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        cartDataSource = LocalCartDataSource(CartDataBase.getInstance(this).cartDAO())

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            navController.navigate(R.id.nav_cart)
        }
        drawer = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_menu, R.id.nav_product_details,
                R.id.nav_tools, R.id.nav_product_list, R.id.nav_cart
            ), drawer
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var headerView = navView.getHeaderView(0)
        var textUser = headerView.findViewById<TextView>(R.id.text_user)
        Common.setSpanString("Bem-vindo ", Common.currentUser?.name, textUser)


        navView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawer.closeDrawers()
            when {
                menuItem.itemId == R.id.nav_logout -> logOut()
                menuItem.itemId == R.id.nav_home -> navController.navigate(R.id.nav_home)
                menuItem.itemId == R.id.nav_menu -> navController.navigate(R.id.nav_menu)
                menuItem.itemId == R.id.nav_tools -> navController.navigate(R.id.nav_tools)
                menuItem.itemId == R.id.nav_cart -> navController.navigate(R.id.nav_cart)
            }

            true
        }
        countCartItem()
    }

    private fun logOut() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sair")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("CANCELAR") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("SAIR") { _, _ ->
                Common.productSelected = null
                Common.categorySelected = null
                Common.currentUser = null

                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onResume() {
        super.onResume()
        countCartItem()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Event Bus

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_product_list)
            Toast.makeText(this, "ENTROU NESSA PESTE em ${event.category.name}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onProductSelected(event: ProductItemClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_product_details)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCountCartEvent(event: CountCartEvent) {
        if (event.isSuccess) {
            countCartItem()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onFABEvente(event: HideFABCart) {
        if (event.isHide) {
            fab.hide()
        } else {
            fab.show()
        }
    }

    private fun countCartItem() {
        cartDataSource.countItemInCart(Common.currentUser?.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int> {
                override fun onSuccess(t: Int) {
                    fab.count = t
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    Toast.makeText(
                        this@HomeActivity,
                        "[COUNT CART]",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }
}
