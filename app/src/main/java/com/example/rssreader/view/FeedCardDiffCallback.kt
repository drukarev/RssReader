package com.example.rssreader.view

import androidx.recyclerview.widget.DiffUtil
import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.ListItemViewModel
import com.example.rssreader.model.ListItemViewModel.*

class FeedCardDiffCallback : DiffUtil.ItemCallback<ListItemViewModel<FeedCardViewModel>>() {

    override fun areItemsTheSame(
        oldItem: ListItemViewModel<FeedCardViewModel>,
        newItem: ListItemViewModel<FeedCardViewModel>
    ): Boolean {
        return when (oldItem) {
            is Data -> (newItem as? Data)?.let { newItem.data.id == oldItem.data.id } == true
            is Progress -> newItem == oldItem
            is Error -> newItem is Error
        }
    }

    override fun areContentsTheSame(
        oldItem: ListItemViewModel<FeedCardViewModel>,
        newItem: ListItemViewModel<FeedCardViewModel>
    ): Boolean {
        return oldItem == newItem
    }
}
