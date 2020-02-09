package com.example.rssreader.model

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ProgressBar
import com.example.rssreader.R
import com.example.rssreader.view.FeedCardView

/**
 * View holder for progress, error or data items in the [FeedAdapter]
 */
sealed class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    class Progress(context: Context) : ListViewHolder(ProgressBar(context).apply {
        isIndeterminate = true
        layoutParams = ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            val offset = resources.getDimensionPixelOffset(R.dimen.spaceS)
            setMargins(offset, offset, offset, offset)
        }
    })

    class Error(context: Context) : ListViewHolder(FeedCardView(context).apply { //TODO: change view
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    })

    class Data(context: Context) : ListViewHolder(FeedCardView(context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    })
}
