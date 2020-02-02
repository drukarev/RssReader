package com.example.rssreader.view

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.model.FeedCardViewModel
import kotlinx.android.synthetic.main.item_feed_card.view.*

class FeedAdapter(
    val onListChanged: () -> Unit
) : PagedListAdapter<FeedCardViewModel, RecyclerView.ViewHolder>(FeedCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): RecyclerView.ViewHolder {
        return Data(parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.itemView.apply {
                title.text = item.title
                author.text = item.author
                date.text = item.date
                sourceFeedName.text = item.sourceFeedName
            }
        } else {
            //TODO: placeholder
            holder.itemView.apply {
                title.text = "---"
                author.text = "---"
                date.text = "---"
                sourceFeedName.text = "---"
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: PagedList<FeedCardViewModel>?,
        currentList: PagedList<FeedCardViewModel>?
    ) {
        super.onCurrentListChanged(previousList, currentList)
        if (previousList != currentList) {
            onListChanged()
        }
    }
}

class Data(context: Context) : RecyclerView.ViewHolder(FeedCardView(context).apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
})
