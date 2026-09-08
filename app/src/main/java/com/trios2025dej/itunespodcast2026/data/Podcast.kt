package com.trios2025dej.itunespodcast2026.data

data class PodcastResponse(
    val resultCount: Int,
    val results: List<Podcast>
)

data class Podcast(
    val collectionName: String,
    val artistName: String,
    val artworkUrl100: String,
    val feedUrl: String,
    val trackId: Long,
    val trackName:String,
)