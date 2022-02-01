package com.example.mobilefirstnewsapp.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilefirstnewsapp.R
import com.example.mobilefirstnewsapp.activities.NewsDescriptionActivity
import com.example.mobilefirstnewsapp.modelclasses.SourcesItem
import com.example.mobilefirstnewsapp.utils.NEWS_URL

class FilterNewsAdapter(
    private val mActivity: Activity,
    private val mSourcesItemList: List<SourcesItem?>?
) :
    RecyclerView.Adapter<FilterNewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView =
            LayoutInflater.from(mActivity).inflate(R.layout.item_news, parent, false)
        return ViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mSourcesItem: SourcesItem? = mSourcesItemList?.get(position);

        //hide image and date fields in case of filter as these are not coming in response of API
        holder.newsIV.visibility = View.GONE
        holder.dateTV.visibility = View.GONE
        holder.view.visibility = View.GONE

        holder.titleTV.setText(mSourcesItem?.description)
        holder.channelNameTV.setText(mSourcesItem?.name)

        holder.itemView.setOnClickListener {
            mActivity.startActivity(
                Intent(mActivity, NewsDescriptionActivity::class.java)
                    .putExtra(NEWS_URL, mSourcesItem?.url)
            )
        }
    }

    override fun getItemCount(): Int {
        return mSourcesItemList?.size!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsIV = itemView.findViewById(R.id.newsIV) as ImageView
        var titleTV = itemView.findViewById(R.id.titleTV) as TextView
        var dateTV = itemView.findViewById(R.id.dateTV) as TextView
        var channelNameTV = itemView.findViewById(R.id.channelNameTV) as TextView
        var view = itemView.findViewById(R.id.view) as View
    }
}