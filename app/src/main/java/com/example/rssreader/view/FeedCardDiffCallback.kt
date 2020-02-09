package com.example.rssreader.view

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.rssreader.model.FeedCardViewModel
import com.example.rssreader.model.PaginationItemViewModel
import com.example.rssreader.model.PaginationItemViewModel.*

class FeedCardDiffCallback : DiffUtil.ItemCallback<PaginationItemViewModel<FeedCardViewModel>>() {

    override fun areItemsTheSame(
        oldItem: PaginationItemViewModel<FeedCardViewModel>,
        newItem: PaginationItemViewModel<FeedCardViewModel>
    ): Boolean {
        return when (oldItem) {
            is Data -> (newItem as? Data)?.let { newItem.data.id == oldItem.data.id } == true
            is Progress -> newItem == oldItem
            is Error -> newItem is Error
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: PaginationItemViewModel<FeedCardViewModel>,
        newItem: PaginationItemViewModel<FeedCardViewModel>
    ): Boolean {
        return oldItem == newItem
    }
}
