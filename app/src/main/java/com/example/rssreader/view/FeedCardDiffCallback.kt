package com.example.rssreader.view

import androidx.recyclerview.widget.DiffUtil
import com.example.rssreader.model.FeedCardModel

class FeedCardDiffCallback : DiffUtil.ItemCallback<FeedCardModel>() {

    override fun areItemsTheSame(oldItem: FeedCardModel, newItem: FeedCardModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedCardModel, newItem: FeedCardModel): Boolean {
        return oldItem == newItem
    }
}
