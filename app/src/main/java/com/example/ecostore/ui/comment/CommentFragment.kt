package com.example.ecostore.ui.comment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecostore.R
import com.example.ecostore.adapter.MyCommentAdapter
import com.example.ecostore.callback.CommentCallBack
import com.example.ecostore.common.Common
import com.example.ecostore.model.CommentModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog

class CommentFragment : BottomSheetDialogFragment(), CommentCallBack {

    private var commentViewModel: CommentViewModel? = null
    private var listener: CommentCallBack?          = null
    private var recyclerViewComment: RecyclerView?  = null
    private var dialog: AlertDialog?                = null

    init {
        listener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_comment_fragment, container, false )

        initViews(itemView)
        loadCommentFromFirebase()

        commentViewModel?.mutableLiveDataCommentList?.observe(this, Observer {
            val adapter = MyCommentAdapter(context!!, it)
            recyclerViewComment?.adapter = adapter
        })
        return itemView
    }

    private fun loadCommentFromFirebase() {
        dialog?.show()

        val commentModelsArray = ArrayList<CommentModel>()

        FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
            .child(Common.productSelected?.id!!)
            .orderByChild("commentTimeStamp")
            .limitToLast(30)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(dbError: DatabaseError) {
                    listener?.onCommentLoadFailed(dbError.message)
                }

                override fun onDataChange(dbSnapshot: DataSnapshot) {
                    for (commentSnapShot in dbSnapshot.children){
                        val commentModel = commentSnapShot.getValue(CommentModel::class.java)
                        commentModel?.let { commentModelsArray.add(it) }
                    }
                    listener?.onCommentLoadSuccess(commentModelsArray)
                }
            })
    }

    private fun initViews(itemView: View?) {

        commentViewModel = ViewModelProviders.of(this).get(CommentViewModel::class.java)

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()

        recyclerViewComment = itemView?.findViewById(R.id.recycler_comment) as RecyclerView
        recyclerViewComment?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        recyclerViewComment?.layoutManager = layoutManager
        recyclerViewComment?.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

    }

    override fun onCommentLoadSuccess(commentModelList: List<CommentModel>) {
        dialog?.dismiss()
        commentViewModel?.setCommentList(commentModelList)
    }

    override fun onCommentLoadFailed(message: String) {
        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    companion object {
        private var instance: CommentFragment? = null

        fun getInstance(): CommentFragment {
            if (instance == null)
                instance = CommentFragment()
            return instance!!
        }
    }
}