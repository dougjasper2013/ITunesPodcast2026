package com.trios2025dej.itunespodcast2026.data

data class Episode(
    val guid: String,
    val title: String,
    val description: String,
    val pubDate: String,
    val mediaUrl: String,
    val mediaType: String,
    val isVideo: Boolean
)