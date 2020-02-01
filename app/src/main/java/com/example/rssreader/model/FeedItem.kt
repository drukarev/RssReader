package com.example.rssreader.model

import androidx.room.*

@Entity
data class FeedItem(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "author") val author: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "source_feed_name") val sourceFeedName: String?
)

@Dao
interface FeedDao {
    @Query("SELECT * FROM FeedItem")
    fun getAll(): List<FeedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<FeedItem>)
}

@Database(entities = [FeedItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}
