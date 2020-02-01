package com.example.rssreader.model

import androidx.room.*
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId

@Entity
data class FeedItem(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "author") val author: String?,
    @ColumnInfo(name = "date") val date: OffsetDateTime?, //TODO: check for date correctness
    @ColumnInfo(name = "source_feed_name") val sourceFeedName: String?
)

@Dao
interface FeedDao {
    @Query("SELECT * FROM FeedItem ORDER BY date DESC ")
    fun getAllSortedByDate(): List<FeedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<FeedItem>)
}

@Database(entities = [FeedItem::class], version = 1)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}

class DatabaseTypeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): OffsetDateTime? {
        return value?.let { OffsetDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault()) }
    }

    @TypeConverter
    fun dateToTimestamp(dateTime: OffsetDateTime?): Long? {
        return dateTime?.toEpochSecond()
    }
}
