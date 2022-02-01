package com.example.mobilefirstnewsapp.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.mobilefirstnewsapp.R
import com.example.mobilefirstnewsapp.adapters.FilterNewsAdapter
import com.example.mobilefirstnewsapp.adapters.NewsAdapter
import com.example.mobilefirstnewsapp.interfaces.PaginationRequestInterface
import com.example.mobilefirstnewsapp.modelclasses.ArticlesItem
import com.example.mobilefirstnewsapp.modelclasses.SourcesItem
import com.example.mobilefirstnewsapp.utils.API_KEY
import com.example.mobilefirstnewsapp.utils.COUNTRY
import com.example.mobilefirstnewsapp.utils.PAGE_SIZE
import com.example.mobilefirstnewsapp.viewmodels.FilterNewsViewModel
import com.example.mobilefirstnewsapp.viewmodels.NewsViewModel
import com.example.mobilefirstnewsapp.viewmodels.SearchNewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    /**
     * Current Activity Instance
     */
    private lateinit var mActivity: MainActivity

    /**
     * ViewModel
     */
    private var viewModel: NewsViewModel? = null
    private var filterViewModel: FilterNewsViewModel? = null
    private var searchViewModel: SearchNewsViewModel? = null

    /**
     * Adapters and LinearLayoutManager
     */
    private var mNewsAdapter: NewsAdapter? = null
    private var mFilterNewsAdapter: FilterNewsAdapter? = null
    private lateinit var mLinearLayoutManager: LinearLayoutManager

    /**
     * lists
     */
    private var mTopHeadlinesList: ArrayList<ArticlesItem?>? = ArrayList()
    private var mFilterNewsList: ArrayList<SourcesItem?>? = ArrayList()

    /**
     * variables and others
     */
    private var mCategory: String = ""
    private var mPageNo = 1
    private var strLastPage = "FALSE"
    private var isSearch = false
    private var isSwipeRefresh = false
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        mActivity = this

        setStatusBar(mActivity)

        initializeObjects()
    }

    private fun initializeObjects() {
        viewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        filterViewModel = ViewModelProviders.of(this).get(FilterNewsViewModel::class.java)
        searchViewModel = ViewModelProviders.of(this).get(SearchNewsViewModel::class.java)

        setEditClick()

        setSwipeRefresh()

        /**
         * execute API to get top headlines
         */
        if (mTopHeadlinesList != null)
            mTopHeadlinesList!!.clear()

        if (mFilterNewsList != null)
            mFilterNewsList!!.clear()

        if (!isNetworkAvailable(mActivity)) {
            showAlertDialog(mActivity, getString(R.string.internet_connection_error))
        } else {
            executeLatestNewsAPI()
        }
    }

    /**
     * execute search API on editor action listener
     */
    private fun setEditClick() {
        searchET.setOnEditorActionListener { v, actionId, event ->

            if (event == null || event.action != KeyEvent.ACTION_DOWN) {
                //do something
                mPageNo = 1
                if (mTopHeadlinesList != null)
                    mTopHeadlinesList!!.clear()

                if (mFilterNewsList != null)
                    mFilterNewsList!!.clear()

                if (!isNetworkAvailable(mActivity)) {
                    showAlertDialog(mActivity, getString(R.string.internet_connection_error))
                } else {
                    executeSearchNewsAPI()
                }
            }
            false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setSwipeRefresh() {
        swipeToRefresh.setColorSchemeResources(R.color.black)
        swipeToRefresh.setOnRefreshListener {

            isSearch = false
            topRL.visibility = View.VISIBLE
            searchRL.visibility = View.GONE

            isSwipeRefresh = true
            mPageNo = 1

            if (mTopHeadlinesList != null)
                mTopHeadlinesList!!.clear()

            mNewsAdapter?.notifyDataSetChanged()

            if (mFilterNewsList != null)
                mFilterNewsList!!.clear()

            mFilterNewsAdapter?.notifyDataSetChanged()

            /**
             * execute API to get top headlines when swiped to refresh
             */
            executeLatestNewsAPI()
        }
    }

    /**
     * execute API to search news
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun executeSearchNewsAPI() {
        if (mPageNo == 1) {
            showProgressDialog(mActivity)
        } else if (mPageNo > 1) {
            mProgressBar.visibility = View.GONE
        }
        searchViewModel?.searchNews(
            mActivity,
            searchET.text.toString(), "2022-02-01", API_KEY,
            PAGE_SIZE.toString(), mPageNo.toString()
        )?.observe(this,
            { mModel ->

                if (mPageNo == 1) {
                    dismissProgressDialog()
                }

                // check list size for pagination
                if (mModel.totalResults!! > 0) {
                    strLastPage =
                        if (mModel.articles == null || mModel.articles.isEmpty() || mModel.articles.size == 0) {
                            "TRUE"
                        } else if (mModel.articles.size < PAGE_SIZE) {
                            "TRUE"
                        } else {
                            "FALSE"
                        }

                    var mTemporaryList: ArrayList<ArticlesItem?>? = ArrayList()

                    if (mModel?.articles != null) {
                        if (mPageNo == 1) {
                            mTopHeadlinesList = mModel.articles
                        } else if (mPageNo > 1) {
                            mTemporaryList = mModel.articles
                        }

                        if (mTemporaryList?.size!! > 1) {
                            mTopHeadlinesList?.addAll(mTemporaryList)
                        }

                        if (mPageNo == 1) {
                            setAdapter()
                        } else {
                            mNewsAdapter?.notifyDataSetChanged()
                        }
                    }
                } else {
                    showAlertDialog(mActivity, "No results found!")
                }
            })
    }

    /**
     * execute API to get top headlines
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun executeLatestNewsAPI() {
        if (!isSwipeRefresh) {
            if (mPageNo == 1) {
                showProgressDialog(mActivity)
            } else if (mPageNo > 1) {
                mProgressBar.visibility = View.GONE
            }
        }
        viewModel?.topHeadlines(
            mActivity,
            COUNTRY, API_KEY, PAGE_SIZE.toString(), mPageNo.toString()
        )?.observe(this,
            { mModel ->
                if (mPageNo == 1) {
                    dismissProgressDialog()
                }

                // check list size for pagination
                if (mModel.totalResults!! > 0) {

                    strLastPage =
                        if (mModel.articles == null || mModel.articles.isEmpty() || mModel.articles.size == 0) {
                            "TRUE"
                        } else if (mModel.articles.size < PAGE_SIZE) {
                            "TRUE"
                        } else {
                            "FALSE"
                        }

                    var mTemporaryList: ArrayList<ArticlesItem?>? = ArrayList()

                    if (mModel?.articles != null) {
                        if (mPageNo == 1) {
                            mTopHeadlinesList = mModel.articles
                        } else if (mPageNo > 1) {
                            mTemporaryList = mModel.articles
                        }

                        if (mTemporaryList?.size!! > 1) {
                            mTopHeadlinesList?.addAll(mTemporaryList)
                        }

                        if (mPageNo == 1) {
                            setAdapter()
                        } else {
                            mNewsAdapter?.notifyDataSetChanged()
                        }
                    }
                } else {
                    showAlertDialog(mActivity, "No results found!")
                }
            })
        if (isSwipeRefresh) {
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun setAdapter() {
        mNewsAdapter = NewsAdapter(mActivity, mTopHeadlinesList, paginationRequestInterface)
        mLinearLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        newsRV.adapter = mNewsAdapter
        newsRV.layoutManager = mLinearLayoutManager
    }

    /**
     * interface used for pagination
     */
    private var paginationRequestInterface: PaginationRequestInterface =
        object : PaginationRequestInterface {
            override fun mPaginationRequestInterface(isLastScrolled: Boolean) {
                if (isLastScrolled) {

                    if (strLastPage == "FALSE") {
                        mProgressBar.visibility = View.VISIBLE
                    }

                    ++mPageNo
                    Handler().postDelayed({
                        if (strLastPage == "FALSE") {
                            if (isSearch) {
                                executeSearchNewsAPI()
                            } else {
                                executeLatestNewsAPI()
                            }
                        } else {
                            mProgressBar.visibility = View.GONE
                        }
                    }, 1000)
                }
            }
        }

    @OnClick(R.id.imgFilterIV, R.id.imgSearchIV, R.id.cancelTV)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.imgFilterIV -> performFilterClick()

            R.id.imgSearchIV -> performSearchClick()

            R.id.cancelTV -> performCancelClick()
        }
    }

    private fun performCancelClick() {
        isSearch = false
        topRL.visibility = View.VISIBLE
        searchRL.visibility = View.GONE
        searchET.setText("")

        mPageNo = 1

        if (mTopHeadlinesList != null)
            mTopHeadlinesList!!.clear()

        if (mFilterNewsList != null)
            mFilterNewsList!!.clear()

        executeLatestNewsAPI()
    }

    private fun performSearchClick() {
        isSearch = true
        topRL.visibility = View.GONE
        searchRL.visibility = View.VISIBLE
    }

    /**
     * show bottom sheet for filtering
     */
    private fun performFilterClick() {
        bottomSheetDialog = BottomSheetDialog(mActivity)

        val view: View = layoutInflater.inflate(R.layout.bottomsheet_filter_layout, null)
        val dialog = BottomSheetDialog(mActivity)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val cancelIV = view.findViewById<ImageView>(R.id.cancelIV)
        val businessTV = view.findViewById<TextView>(R.id.businessTV)
        val entertainmentTV = view.findViewById<TextView>(R.id.entertainmentTV)
        val generalTV = view.findViewById<TextView>(R.id.generalTV)
        val healthTV = view.findViewById<TextView>(R.id.healthTV)
        val scienceTV = view.findViewById<TextView>(R.id.scienceTV)
        val sportsTV = view.findViewById<TextView>(R.id.sportsTV)
        val technologyTV = view.findViewById<TextView>(R.id.technologyTV)

        cancelIV.setOnClickListener { dialog.dismiss() }

        businessTV.setOnClickListener {
            dialog.dismiss()
            mCategory = "business"

            clearAllLists()
            executeFilterNewsAPI()
        }

        entertainmentTV.setOnClickListener {
            dialog.dismiss()
            mCategory = "entertainment"

            clearAllLists()
            executeFilterNewsAPI()
        }

        generalTV.setOnClickListener {
            dialog.dismiss()
            mCategory = "general"

            clearAllLists()
            executeFilterNewsAPI()
        }

        healthTV.setOnClickListener {
            dialog.dismiss()
            mCategory = "health"

            clearAllLists()
            executeFilterNewsAPI()
        }

        scienceTV.setOnClickListener {
            dialog.dismiss()

            mCategory = "science"

            clearAllLists()
            executeFilterNewsAPI()
        }

        sportsTV.setOnClickListener {
            dialog.dismiss()
            mCategory = "sports"

            clearAllLists()
            executeFilterNewsAPI()
        }

        technologyTV.setOnClickListener {
            dialog.dismiss()
            mCategory = "technology"

            clearAllLists()
            executeFilterNewsAPI()
        }

        dialog.show()
    }

    /**
     * execute filter API
     */
    private fun executeFilterNewsAPI() {
        filterViewModel?.filterNews(
            mActivity,
            mCategory,
            API_KEY
        )?.observe(this,
            { mModel ->

                if (mModel.sources?.isNotEmpty()!!) {
                    mFilterNewsList = mModel.sources
                    setFilterAdapter()
                } else {
                    showAlertDialog(mActivity, "No results found!")
                }
            })
    }

    private fun setFilterAdapter() {
        mFilterNewsAdapter = FilterNewsAdapter(mActivity, mFilterNewsList)
        mLinearLayoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        newsRV.adapter = mFilterNewsAdapter
        newsRV.layoutManager = mLinearLayoutManager
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAllLists() {
        if (mTopHeadlinesList != null)
            mTopHeadlinesList!!.clear()

        mNewsAdapter?.notifyDataSetChanged()

        if (mFilterNewsList != null)
            mFilterNewsList!!.clear()

        mFilterNewsAdapter?.notifyDataSetChanged()
    }
}