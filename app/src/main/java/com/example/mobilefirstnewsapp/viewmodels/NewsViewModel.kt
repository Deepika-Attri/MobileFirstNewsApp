package com.example.mobilefirstnewsapp.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobilefirstnewsapp.activities.BaseActivity
import com.example.mobilefirstnewsapp.modelclasses.ErrorNewsResponse
import com.example.mobilefirstnewsapp.modelclasses.TopHeadlinesResponse
import com.example.mobilefirstnewsapp.retrofit.Api
import com.example.mobilefirstnewsapp.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import com.google.gson.GsonBuilder

class NewsViewModel : ViewModel() {
    private var mTopHeadlinesResponse: MutableLiveData<TopHeadlinesResponse>? = null

    fun topHeadlines(
        activity: Activity?,
        country: String?,
        apiKey: String?,
        pageSize: String?,
        page: String?
    ): LiveData<TopHeadlinesResponse>? {

        mTopHeadlinesResponse = MutableLiveData<TopHeadlinesResponse>()

        val api: Api? = ApiClient().getApiClient()?.create(Api::class.java)
        api?.topHeadlines(country, apiKey, pageSize, page)
            ?.enqueue(object : Callback<TopHeadlinesResponse?> {
                override fun onResponse(
                    call: Call<TopHeadlinesResponse?>?,
                    response: Response<TopHeadlinesResponse?>
                ) {
                    if (response.body() != null && response.code() == 200) {
                        BaseActivity().dismissProgressDialog()
                        mTopHeadlinesResponse!!.value = response.body()
                    } else {
                        BaseActivity().dismissProgressDialog()

                        val gson = GsonBuilder().create()
                        val mError: ErrorNewsResponse
                        try {
                            mError = gson.fromJson(
                                response.errorBody()!!.string(),
                                ErrorNewsResponse::class.java
                            )
                            BaseActivity().showAlertDialog(
                                activity,
                                mError.message
                            )
                        } catch (e: IOException) {
                            // handle failure to read error
                        }
                    }
                }

                override fun onFailure(call: Call<TopHeadlinesResponse?>?, t: Throwable?) {
                    BaseActivity().dismissProgressDialog()
                    Log.e("onFailure", t.toString())
                }
            })
        return mTopHeadlinesResponse
    }
}