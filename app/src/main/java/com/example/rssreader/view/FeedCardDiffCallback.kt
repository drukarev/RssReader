package com.example.rssreader.view

import androidx.recyclerview.widget.DiffUtil
import com.example.rssreader.model.FeedCardViewModel

class FeedCardDiffCallback : DiffUtil.ItemCallback<FeedCardViewModel>() {

    override fun areItemsTheSame(oldItem: FeedCardViewModel, newItem: FeedCardViewModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedCardViewModel, newItem: FeedCardViewModel): Boolean {
        return oldItem == newItem
    }
}
