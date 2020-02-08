package com.example.rssreader.view

import androidx.recyclerview.widget.DiffUtil
import com.example.rssreader.model.ListItemViewModel
import com.example.rssreader.model.ListItemViewModel.*

class FeedCardDiffCallback : DiffUtil.ItemCallback<ListItemViewModel>() {

    override fun areItemsTheSame(oldItem: ListItemViewModel, newItem: ListItemViewModel): Boolean {
        return when (oldItem) {
            is Data -> (newItem as? Data)?.let { newItem.data.id == oldItem.data.id } == true
            is Progress -> newItem == oldItem
            is Error -> newItem is Error
        }
    }

    override fun areContentsTheSame(oldItem: ListItemViewModel, newItem: ListItemViewModel): Boolean {
        return oldItem == newItem
    }
}
