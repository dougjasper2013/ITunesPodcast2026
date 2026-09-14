package com.trios2025dej.itunespodcast2026.data

import android.util.Xml
import java.net.HttpURLConnection
import java.net.URL
import org.xmlpull.v1.XmlPullParser

object PodcastRssParser {
    fun parseFeed(feedUrl: String): List<Episode> {

        val episodes = mutableListOf<Episode>()

        val connection =
            URL(feedUrl).openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0"
        )

        try {

            if (connection.responseCode !in 200..299) {
                return emptyList()
            }

            val parser = Xml.newPullParser()

            connection.inputStream.use { inputStream ->

                parser.setInput(inputStream, null)

                var eventType = parser.eventType

                var insideItem = false

                var title = ""
                var description = ""
                var pubDate = ""
                var mediaUrl = ""
                var mediaType = ""

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    when (eventType) {

                        XmlPullParser.START_TAG -> {

                            val tagName = parser.name.lowercase()

                            if (tagName == "item") {

                                insideItem = true

                                title = ""
                                description = ""
                                pubDate = ""
                                mediaUrl = ""
                                mediaType = ""

                            } else if (insideItem) {

                                when (tagName) {

                                    "title" -> {
                                        title = parser.nextText()
                                    }

                                    "description" -> {
                                        description = parser.nextText()
                                    }

                                    "content:encoded" -> {
                                        description = parser.nextText()
                                    }

                                    "pubdate" -> {
                                        pubDate = parser.nextText()
                                    }

                                    "enclosure" -> {

                                        val url =
                                            parser.getAttributeValue(
                                                null,
                                                "url"
                                            )

                                        val type =
                                            parser.getAttributeValue(
                                                null,
                                                "type"
                                            )

                                        if (!url.isNullOrBlank()) {
                                            mediaUrl = url
                                        }

                                        if (!type.isNullOrBlank()) {
                                            mediaType = type
                                        }
                                    }

                                    "media:content" -> {

                                        val url =
                                            parser.getAttributeValue(
                                                null,
                                                "url"
                                            )

                                        val type =
                                            parser.getAttributeValue(
                                                null,
                                                "type"
                                            )

                                        if (!url.isNullOrBlank()) {
                                            mediaUrl = url
                                        }

                                        if (!type.isNullOrBlank()) {
                                            mediaType = type
                                        }
                                    }
                                }
                            }
                        }

                        XmlPullParser.END_TAG -> {

                            val tagName = parser.name.lowercase()

                            if (tagName == "item" && insideItem) {

                                if (mediaUrl.isNotBlank()) {

                                    episodes.add(
                                        Episode(
                                            title = title.ifBlank {
                                                "Untitled Episode"
                                            },
                                            description = description,
                                            pubDate = pubDate,
                                            mediaUrl = mediaUrl,
                                            mediaType = mediaType,
                                            isVideo = isVideoMedia(
                                                mediaType,
                                                mediaUrl
                                            )
                                        )
                                    )
                                }

                                insideItem = false
                            }
                        }
                    }

                    eventType = parser.next()
                }
            }

        } finally {
            connection.disconnect()
        }

        return episodes
    }

    fun determinePodcastType(
        feedUrl: String
    ): String {

        return try {

            val episodes = parseFeed(feedUrl)

            if (episodes.isEmpty()) {
                "Unknown"
            } else {

                val hasVideo = episodes.any {
                    it.isVideo
                }

                val hasAudio = episodes.any {
                    !it.isVideo
                }

                when {
                    hasVideo && hasAudio -> "Audio / Video"
                    hasVideo -> "Video"
                    hasAudio -> "Audio"
                    else -> "Unknown"
                }
            }

        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun isVideoMedia(
        mediaType: String,
        mediaUrl: String
    ): Boolean {

        val type = mediaType.lowercase()

        if (type.startsWith("video/")) {
            return true
        }

        if (type.startsWith("audio/")) {
            return false
        }

        val url = mediaUrl
            .lowercase()
            .substringBefore("?")

        return url.endsWith(".mp4") ||
                url.endsWith(".m4v") ||
                url.endsWith(".mov") ||
                url.endsWith(".webm") ||
                url.endsWith(".mkv")
    }
}