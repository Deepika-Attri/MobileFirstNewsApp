package com.example.mobilefirstnewsapp.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilefirstnewsapp.R
import com.example.mobilefirstnewsapp.activities.NewsDescriptionActivity
import com.example.mobilefirstnewsapp.interfaces.PaginationRequestInterface
import com.example.mobilefirstnewsapp.modelclasses.ArticlesItem
import com.example.mobilefirstnewsapp.utils.NEWS_URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NewsAdapter(
    private val mActivity: Activity,
    private val mTopHeadlinesList: List<ArticlesItem?>?,
    private val paginationRequestInterface: PaginationRequestInterface
) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView =
            LayoutInflater.from(mActivity).inflate(R.layout.item_news, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mArticlesItem: ArticlesItem? = mTopHeadlinesList?.get(position)!!

        // using for pagination
        if (position >= mTopHeadlinesList.size - 1) {
            paginationRequestInterface.mPaginationRequestInterface(true)
        }

        Glide.with(mActivity).load(mArticlesItem?.urlToImage).into(holder.newsIV)
        holder.titleTV.text = mArticlesItem?.title
        holder.channelNameTV.text = mArticlesItem?.source?.name
        holder.dateTV.text = mArticlesItem?.publishedAt?.let { covertTimeToText(it) }

        holder.itemView.setOnClickListener {
            mActivity.startActivity(
                Intent(mActivity, NewsDescriptionActivity::class.java)
                    .putExtra(NEWS_URL, mArticlesItem?.url)
            )
        }
    }

    override fun getItemCount(): Int {
        return mTopHeadlinesList?.size!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsIV = itemView.findViewById(R.id.newsIV) as ImageView
        var titleTV = itemView.findViewById(R.id.titleTV) as TextView
        var dateTV = itemView.findViewById(R.id.dateTV) as TextView
        var channelNameTV = itemView.findViewById(R.id.channelNameTV) as TextView
    }

    @SuppressLint("SimpleDateFormat")
    private fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val suffix = "Ago"
        try {
            /* convert date to another format */
            var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val result1: Date = spf.parse(dataDate)
            spf = SimpleDateFormat("dd MMM yyyy")
            val convertedDate = spf.format(result1)

            /* convert date to time ago format */
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 1) {
                convTime = convertedDate
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.toString())
        }
        return convTime
    }
}