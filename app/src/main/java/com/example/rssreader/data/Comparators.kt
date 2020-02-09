package com.example.rssreader.data

import com.example.rssreader.model.FeedItem

fun getSortByDataComparator(): Comparator<FeedItem> {
    return Comparator { o1, o2 ->
        if (o1.id == o2.id) {
            0
        } else {
            o2.date.compareTo(o1.date) // reversed comparison to display new items first
        }
    }
}
