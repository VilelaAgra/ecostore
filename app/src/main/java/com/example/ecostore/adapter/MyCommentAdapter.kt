package com.example.ecostore.adapter

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecostore.R
import com.example.ecostore.model.CommentModel

class MyCommentAdapter(
    internal var context: Context,
    internal var commentList: List<CommentModel>
) : RecyclerView.Adapter<MyCommentAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_comment_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val timeStamp = commentList[position].commentTimeStamp!!["timeStamp"].toString().toLong()
        holder.commentDate?.text = DateUtils.getRelativeTimeSpanString(timeStamp)
        holder.commentName?.text = commentList[position].name
        holder.commentComment?.text = commentList[position].comment
        holder.commentRating?.rating = commentList[position].ratingValue

    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var commentName: TextView? = null
        var commentDate: TextView? = null
        var commentComment: TextView? = null
        var commentRating: RatingBar? = null

        init {
            commentName = itemView.findViewById(R.id.comment_text_view_name) as TextView
            commentDate = itemView.findViewById(R.id.comment_text_view_date) as TextView
            commentComment = itemView.findViewById(R.id.comment_text_view_comment) as TextView
            commentRating = itemView.findViewById(R.id.comment_rating_bar) as RatingBar
        }
    }
}