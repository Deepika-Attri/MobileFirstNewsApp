package com.example.mobilefirstnewsapp.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobilefirstnewsapp.activities.BaseActivity
import com.example.mobilefirstnewsapp.modelclasses.ErrorNewsResponse
import com.example.mobilefirstnewsapp.modelclasses.FilterNewsResponse
import com.example.mobilefirstnewsapp.retrofit.Api
import com.example.mobilefirstnewsapp.retrofit.ApiClient
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class FilterNewsViewModel : ViewModel() {
    private var mFilterNewsResponse: MutableLiveData<FilterNewsResponse>? = null

    fun filterNews(
        activity: Activity?,
        category: String?,
        apiKey: String?
    ): LiveData<FilterNewsResponse>? {

        mFilterNewsResponse = MutableLiveData<FilterNewsResponse>()
        BaseActivity().showProgressDialog(activity)

        val api: Api? = ApiClient().getApiClient()?.create(Api::class.java)
        api?.filterNews(category, apiKey)
            ?.enqueue(object : Callback<FilterNewsResponse?> {
                override fun onResponse(
                    call: Call<FilterNewsResponse?>?,
                    response: Response<FilterNewsResponse?>
                ) {
                    BaseActivity().dismissProgressDialog()
                    if (response.body() != null && response.code() == 200) {
                        BaseActivity().dismissProgressDialog()
                        mFilterNewsResponse!!.value = response.body()
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

                override fun onFailure(call: Call<FilterNewsResponse?>?, t: Throwable?) {
                    BaseActivity().dismissProgressDialog()
                    Log.e("onFailure", t.toString())
                }
            })
        return mFilterNewsResponse
    }
}