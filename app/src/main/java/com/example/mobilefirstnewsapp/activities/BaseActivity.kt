package com.example.mobilefirstnewsapp.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilefirstnewsapp.R

open class BaseActivity : AppCompatActivity() {
    /**
     * set white status bar
     */
    fun setStatusBar(mActivity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mActivity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            mActivity.window.statusBarColor = resources.getColor(R.color.white)
        }
    }

    /**
     * Check Internet Connections
     */
    fun isNetworkAvailable(mContext: Context): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Toast Message
     */
    fun showToast(mActivity: Activity?, strMessage: String?) {
        Toast.makeText(mActivity, strMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        lateinit var progressDialog: Dialog
    }

    /**
     * Show Progress Dialog
     */
    fun showProgressDialog(mActivity: Activity?) {
        progressDialog = Dialog(mActivity!!)
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    /**
     * Hide Progress Dialog
     */
    fun dismissProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    /**
     * Error Alert Dialog
     */
    fun showAlertDialog(mActivity: Activity?, strMessage: String?) {
        val alertDialog = Dialog(mActivity!!)
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setContentView(R.layout.dialog_alert)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        val txtMessageTV = alertDialog.findViewById<TextView>(R.id.txtMessageTV)
        val btnDismiss = alertDialog.findViewById<TextView>(R.id.btnDismiss)
        txtMessageTV.text = strMessage
        btnDismiss.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }
}