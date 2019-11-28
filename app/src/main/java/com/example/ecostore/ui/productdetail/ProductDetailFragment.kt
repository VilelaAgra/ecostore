package com.example.ecostore.ui.productdetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.ecostore.R
import com.example.ecostore.common.Common
import com.example.ecostore.model.CommentModel
import com.example.ecostore.model.ProductModel
import com.example.ecostore.ui.comment.CommentFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog

class ProductDetailFragment : Fragment() {

    private lateinit var productDetailViewModel: ProductDetailViewModel

    private var imageProduct: ImageView? = null
    private var buttonCart: CounterFab? = null
    private var buttonRating: FloatingActionButton? = null
    private var nameProduct: TextView? = null
    private var priceProduct: TextView? = null
    private var descriptionProduct: TextView? = null
    private var buttonNumber: ElegantNumberButton? = null
    private var ratingBar: RatingBar? = null
    private var buttonShowComment: Button? = null
    private var waitingDialog: android.app.AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        productDetailViewModel =
            ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_product_detail, container, false)

        initViews(root)

        productDetailViewModel.getMutableLiveDataProduct().observe(this, Observer {
            displayInfo(it)
        })

        productDetailViewModel.getMutableLiveDataComment().observe(this, Observer {
            submitRatingToFirebase(it)
        })

        return root
    }

    private fun submitRatingToFirebase(commentModel: CommentModel?) {
        waitingDialog?.show()

        //submit comment Ref
        FirebaseDatabase.getInstance()
            .getReference(Common.COMMENT_REF)
            .child(Common.productSelected!!.id!!)
            .push()
            .setValue(commentModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    addRatingToProduct(commentModel!!.ratingValue.toDouble())
                }
                waitingDialog?.dismiss()
            }
    }

    private fun addRatingToProduct(ratingValue: Double) {
        FirebaseDatabase.getInstance()
            .getReference(Common.CATEGORY_REF)
            .child(Common.categorySelected?.menu_id!!)
            .child("products")
            .child(Common.productSelected?.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(dbError: DatabaseError) {
                    waitingDialog!!.dismiss()
                    Toast.makeText(context!!, "" + dbError.message, Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(dbSnapShot: DataSnapshot) {
                    if (dbSnapShot.exists()) {
                        val productModel = dbSnapShot.getValue(ProductModel::class.java)
                        productModel!!.key = Common.productSelected!!.key
                        //Add key

                        val sumRating = productModel.ratingValue!!.toDouble() + ratingValue
                        val ratingCount = productModel.ratingCount + 1
                        val result = sumRating / ratingCount

                        val updateData = HashMap<String, Any>()
                        updateData["ratingValue"] = result
                        updateData["ratingCount"] = ratingValue

                        //Update Data na variabe
                        productModel.ratingCount = ratingCount
                        productModel.ratingValue = result

                        dbSnapShot.ref
                            .updateChildren(updateData)
                            .addOnCompleteListener {
                                waitingDialog?.dismiss()
                                if (it.isSuccessful) {
                                    Common.productSelected = productModel
                                    productDetailViewModel.setProductModel(productModel)
                                    Toast.makeText(context!!, "Obrigado", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        waitingDialog?.dismiss()
                    }

                }

            })
    }

    private fun displayInfo(productModel: ProductModel?) {
        context?.let {
            Glide
                .with(it)
                .load(productModel?.image)
                .into(imageProduct!!)
        }
        nameProduct?.text = productModel?.name?.let { StringBuffer(it) }
        priceProduct?.text = productModel?.price?.let { StringBuffer(it.toString()) }
        descriptionProduct?.text = productModel?.description?.let { StringBuffer(it) }

        ratingBar!!.rating = productModel!!.ratingValue.toFloat()
    }

    private fun initViews(root: View?) {

        waitingDialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()

        nameProduct = root?.findViewById(R.id.product_details_text_view_product_name) as TextView
        imageProduct = root.findViewById(R.id.product_details_image) as ImageView
        priceProduct = root.findViewById(R.id.product_details_text_view_price) as TextView
        descriptionProduct = root.findViewById(R.id.product_details_product_description) as TextView

        buttonCart = root.findViewById(R.id.product_details_button_cart) as CounterFab
        buttonRating = root.findViewById(R.id.product_details_button_rating) as FloatingActionButton
        buttonNumber = root.findViewById(R.id.product_details_number_button) as ElegantNumberButton
        ratingBar = root.findViewById(R.id.product_details_rating_bar) as RatingBar
        buttonShowComment = root.findViewById(R.id.product_details_button_show_comment) as Button

        //EventBus
        buttonRating?.setOnClickListener {
            showDialogRating()
        }

        buttonShowComment?.setOnClickListener {
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(activity!!.supportFragmentManager, "CommentFragment")
        }

    }

    private fun showDialogRating() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Avaliando Produto")
        builder.setMessage("Por favor preencha as informações")

        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment, null)

        val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_comment_rating_bar)
        val editTextComment = itemView.findViewById<EditText>(R.id.rating_comment_edit_text_comment)

        builder.setView(itemView)
        builder.setNegativeButton("CANCELAR") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.setPositiveButton("ENVIAR COMENTÁRIO") { _, _ ->

            val commentModel = CommentModel()
            commentModel.name = Common.currentUser?.name
            commentModel.uid = Common.currentUser?.uid
            commentModel.comment = editTextComment.text.toString()
            commentModel.ratingValue = ratingBar.rating

            val serverTimeStamp = HashMap<String, Any>()
            serverTimeStamp["timeStamp"] = ServerValue.TIMESTAMP
            commentModel.commentTimeStamp = (serverTimeStamp)

            productDetailViewModel.setCommentModel(commentModel)

        }
        val dialog = builder.create()
        dialog.show()
    }
}


