package com.example.rssreader.model

import android.content.Context
import androidx.paging.DataSource
import androidx.room.*
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId

@Entity(tableName = "feed", indices = [Index(value = ["date"])])
data class FeedItem(
    @PrimaryKey @ColumnInfo(name = "id") val uid: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "author") val author: String?,
    @ColumnInfo(name = "date") val date: OffsetDateTime?, //TODO: check for date correctness
    @ColumnInfo(name = "source_feed_name") val sourceFeedName: String?
)

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed ORDER BY date DESC ")
    fun feedByDate(): DataSource.Factory<Int, FeedItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<FeedItem>)
}

@Database(entities = [FeedItem::class], version = 1)
@TypeConverters(DatabaseTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao

    companion object { //TODO: move to dependency injection
        private var instance: AppDatabase? = null
        @Synchronized
        fun get(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "feeds"
                ).build()

            }
            return instance!!
        }
    }
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
