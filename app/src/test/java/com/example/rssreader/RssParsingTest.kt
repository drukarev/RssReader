package com.example.rssreader

import com.example.rssreader.model.Failure.InvalidRssXml
import com.example.rssreader.model.FeedItem
import com.example.rssreader.model.Response
import com.example.rssreader.model.Response.Fail
import com.example.rssreader.model.Response.Result
import com.example.rssreader.parser.parseAsRss
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

@RunWith(ParameterizedRobolectricTestRunner::class)
class RssParsingTest(
    private val testXml: String,
    private val expectedResponse: Response<List<FeedItem>>
) {

    companion object {
        @[ParameterizedRobolectricTestRunner.Parameters(name = "{0},{1}") JvmStatic]
        fun data(): Collection<Array<Any>> {
            return listOf(
                arrayOf("", Fail(InvalidRssXml)),
                arrayOf("rss", Fail(InvalidRssXml)),
                arrayOf(
                    """<rss version="2.0">
                         <title>TestChannelTitle</title>
                         <description>TestDescription</description>
                         <item>
                          <title>TestItemTitle</title>
                          <guid isPermaLink="false">TestId</guid>
                          <pubDate>Sun, 06 Sep 2009 12:00:00 +0000</pubDate>
                          <creator>TestAuthor</creator>
                         </item>
                        </rss>""", Fail(InvalidRssXml)
                ),
                arrayOf(
                    """<rss version="2.0">
                        <channel>
                         <title>TestChannelTitle</title>
                         <description>TestDescription</description>
                         <item>
                          <title>TestItemTitle</title>
                          <guid isPermaLink="false">TestId</guid>
                          <pubDate>Sun, 06 Sep 2009 12:00:00 +0000</pubDate>
                          <creator>TestAuthor</creator>
                         </item>
                        </channel>
                        </rss>""", Result(
                        listOf(
                            FeedItem(
                                id = "TestId",
                                title = "TestItemTitle",
                                author = "TestAuthor",
                                date = OffsetDateTime.of(2009, 9, 6, 12, 0, 0, 0, ZoneOffset.UTC),
                                sourceFeedName = "TestChannelTitle"
                            )
                        )
                    )
                ),
                arrayOf(
                    """<rss version="2.0">
                        <channel>
                         <title>TestChannelTitle</title>
                         <description>TestDescription</description>
                         <item>
                          <title>TestItemTitle1</title>
                          <guid isPermaLink="false">TestId1</guid>
                          <pubDate>Sun, 06 Sep 2009 12:00:00 +0000</pubDate>
                          <creator>TestAuthor1</creator>
                         </item>
                          <item>
                          <title>TestItemTitle2</title>
                          <guid isPermaLink="false">TestId2</guid>
                          <pubDate>Sun, 06 Sep 2009 12:00:00 +0000</pubDate>
                          <creator>TestAuthor2</creator>
                         </item>
                          <item>
                          <title>TestItemTitle3</title>
                          <guid isPermaLink="false">TestId3</guid>
                          <pubDate>Sun, 06 Sep 2009 12:00:00 +0000</pubDate>
                          <creator>TestAuthor3</creator>
                         </item>
                        </channel>
                        </rss>""", Result(
                        listOf(
                            FeedItem(
                                id = "TestId1",
                                title = "TestItemTitle1",
                                author = "TestAuthor1",
                                date = OffsetDateTime.of(2009, 9, 6, 12, 0, 0, 0, ZoneOffset.UTC),
                                sourceFeedName = "TestChannelTitle"
                            ),
                            FeedItem(
                                id = "TestId2",
                                title = "TestItemTitle2",
                                author = "TestAuthor2",
                                date = OffsetDateTime.of(2009, 9, 6, 12, 0, 0, 0, ZoneOffset.UTC),
                                sourceFeedName = "TestChannelTitle"
                            ),
                            FeedItem(
                                id = "TestId3",
                                title = "TestItemTitle3",
                                author = "TestAuthor3",
                                date = OffsetDateTime.of(2009, 9, 6, 12, 0, 0, 0, ZoneOffset.UTC),
                                sourceFeedName = "TestChannelTitle"
                            )
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `should parse xml into response`() {
        assertEquals(expectedResponse, parseAsRss(testXml.byteInputStream()))
    }
}
