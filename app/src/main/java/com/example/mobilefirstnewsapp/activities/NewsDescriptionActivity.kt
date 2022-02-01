package com.example.mobilefirstnewsapp.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.mobilefirstnewsapp.R
import com.example.mobilefirstnewsapp.utils.NEWS_URL
import kotlinx.android.synthetic.main.activity_news_description.*

class NewsDescriptionActivity : BaseActivity() {
    /**
     * Current Activity Instance
     */
    private lateinit var mActivity: NewsDescriptionActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_description)

        ButterKnife.bind(this)

        mActivity = this

        setStatusBar(mActivity)

        getIntentData()
    }

    /**
     * get data from previous activity
     */
    private fun getIntentData() {
        if (intent != null) {
            if (intent.getStringExtra(NEWS_URL) != null) {
                val newURL: String? = intent.getStringExtra(NEWS_URL)

                //load url in web-view
                newURL?.let { loadUrlInWebView(it) }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.backRL)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.backRL -> onBackPressed()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrlInWebView(strLinkUrl: String) {
        mWebViewWV?.webViewClient = MyWebClient()
        mWebViewWV?.settings?.javaScriptEnabled = true
        mWebViewWV?.loadUrl(strLinkUrl)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    class MyWebClient : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebViewWV!!.canGoBack()) {
            mWebViewWV!!.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}