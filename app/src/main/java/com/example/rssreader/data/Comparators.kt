package com.example.rssreader.data

import com.example.rssreader.model.FeedItem

fun getSortByDateComparator(): Comparator<FeedItem> {
    return Comparator { o1, o2 ->
        if (o1.id == o2.id) {
            0
        } else {
            o2.date.compareTo(o1.date) // reversed comparison to display new items first
        }
    }
}

/**
 * First sorts by FeedItem page and then by date, so items always stay grouped as pages.
 */
fun getSortByPageAndDateComparator(): Comparator<FeedItem> {
    return Comparator { o1, o2 ->
        if (o1.id == o2.id) {
            0
        } else {
            val pageComparison = o1.page.compareTo(o2.page)
            if (pageComparison != 0) {
                pageComparison
            } else {
                o2.date.compareTo(o1.date) // reversed comparison to display new items first
            }
        }
    }
}
