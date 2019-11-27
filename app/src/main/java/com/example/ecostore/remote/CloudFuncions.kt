package com.example.ecostore.remote

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query


interface CloudFuncions {

    @GET("getCustomToken")
    fun getCustomToken(@Query("access_token") accessToken: String) : Observable<ResponseBody>
}