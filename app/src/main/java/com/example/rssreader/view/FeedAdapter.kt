package com.example.rssreader.view

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.model.FeedCardModel
import kotlinx.android.synthetic.main.item_feed_card.view.*

class FeedAdapter : ListAdapter<FeedCardModel, RecyclerView.ViewHolder>(
    AsyncDifferConfig.Builder(FeedCardDiffCallback()).build()
) {

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): RecyclerView.ViewHolder {
        return Data(parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.apply {
            title.text = item.title
            author.text = item.author
            date.text = item.date
            sourceFeedName.text = item.sourceFeedName
        }
    }
}

class Data(context: Context) : RecyclerView.ViewHolder(FeedCardView(context).apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
})
