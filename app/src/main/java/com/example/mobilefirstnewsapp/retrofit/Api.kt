package com.example.mobilefirstnewsapp.retrofit

import com.example.mobilefirstnewsapp.modelclasses.FilterNewsResponse
import com.example.mobilefirstnewsapp.modelclasses.TopHeadlinesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("top-headlines")
    fun topHeadlines(
        @Query("country") country: String?,
        @Query("apiKey") apiKey: String?,
        @Query("pageSize") pageSize: String?,
        @Query("page") page: String?
    ): Call<TopHeadlinesResponse?>?

    @GET("everything")
    fun searchNews(
        @Query("qInTitle") qInTitle: String?,
        @Query("from") from: String?,
        @Query("apiKey") apiKey: String?,
        @Query("pageSize") pageSize: String?,
        @Query("page") page: String?
    ): Call<TopHeadlinesResponse?>?

    @GET("sources")
    fun filterNews(
        @Query("category") category: String?,
        @Query("apiKey") apiKey: String?
    ): Call<FilterNewsResponse?>?
}