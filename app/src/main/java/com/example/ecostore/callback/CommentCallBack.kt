package com.example.ecostore.callback

import com.example.ecostore.model.CommentModel

interface CommentCallBack {
    fun onCommentLoadSuccess(commentModelList: List<CommentModel>)
    fun onCommentLoadFailed(message: String)
}