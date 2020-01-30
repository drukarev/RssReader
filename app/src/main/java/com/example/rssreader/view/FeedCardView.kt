package com.example.rssreader.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import com.example.rssreader.R
import kotlinx.android.synthetic.main.item_feed_card.view.*

class FeedCardView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        cardElevation = resources.getDimensionPixelOffset(R.dimen.cardElevation).toFloat()
        radius = resources.getDimensionPixelOffset(R.dimen.cardRadius).toFloat()
        addView(View.inflate(context, R.layout.item_feed_card, null))
    }

    fun setTitle(titleText: CharSequence) {
        title.text = titleText
    }

    fun setDate(dateText: CharSequence) {
        date.text = dateText
    }

    fun setAuthor(authorText: CharSequence) {
        author.text = authorText
    }

    fun setSourceFeedName(sourceFeedNameText: CharSequence) {
        sourceFeedName.text = sourceFeedNameText
    }
}
