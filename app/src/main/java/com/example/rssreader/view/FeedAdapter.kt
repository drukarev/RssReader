package com.example.rssreader.pagination

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rssreader.R
import com.example.rssreader.model.*
import com.example.rssreader.view.FeedCardDiffCallback
import kotlinx.android.synthetic.main.item_feed_card.view.*

class FeedAdapter(
    private val onReloadClick: () -> Unit
) : ListAdapter<PaginationItemViewModel<FeedCardViewModel>, RecyclerView.ViewHolder>(FeedCardDiffCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PaginationItemViewModel.Data -> ItemViewType.DATA.id
        is PaginationItemViewModel.Progress -> ItemViewType.PROGRESS.id
        is PaginationItemViewModel.Error -> ItemViewType.ERROR.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): ListViewHolder =
        when (ItemViewType.values()[itemViewType]) {
            ItemViewType.DATA -> ListViewHolder.Data(parent.context)
            ItemViewType.PROGRESS -> ListViewHolder.Progress(parent.context)
            ItemViewType.ERROR -> ListViewHolder.Error(parent.context).apply {
                itemView.setOnClickListener { onReloadClick() }
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PaginationItemViewModel.Data -> {
                holder.itemView.apply {
                    title.text = item.data.title
                    author.text = item.data.author
                    date.text = item.data.date
                    sourceFeedName.text = item.data.sourceFeedName
                }
            }
            is PaginationItemViewModel.Error -> {
                holder.itemView.apply {
                    title.text = item.errorText
                    author.text = context.getString(R.string.button_reload)
                    date.text = "---"
                    sourceFeedName.text = "---"
                }
            }
        }
    }

    enum class ItemViewType(val id: Int) {
        DATA(0),
        ERROR(1),
        PROGRESS(2),
    }
}
