package com.example.rssreader.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import com.example.rssreader.R

class ErrorCardView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        cardElevation = resources.getDimensionPixelOffset(R.dimen.cardElevation).toFloat()
        radius = resources.getDimensionPixelOffset(R.dimen.cardRadius).toFloat()
        addView(View.inflate(context, R.layout.item_error_card, null))
    }
}
