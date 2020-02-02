package com.example.rssreader.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import org.threeten.bp.format.DateTimeFormatter

data class FeedCardViewModel(
    val id: String,
    val title: CharSequence,
    val author: CharSequence,
    val date: CharSequence,
    val sourceFeedName: CharSequence
)


class FeedViewModel(app: Application) : AndroidViewModel(app) {
    val feed: LiveData<PagedList<FeedCardViewModel>> =
        AppDatabase.get(app).feedDao().feedByDate().map {
            FeedCardViewModel(
                id = it.uid,
                title = it.title ?: "No title",
                author = it.author ?: "No author",
                date = it.date?.format(DateTimeFormatter.ofPattern("dd MMMM, HH:mm")) ?: "No date",
                sourceFeedName = it.sourceFeedName ?: "SourceFeedName"
            )
        }.toLiveData(pageSize = 50)
}
