package com.trios2025dej.itunespodcast2026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscribed_podcasts")
data class SubscribedPodcast(

    @PrimaryKey
    val trackId: Long,

    val collectionName: String,

    val artistName: String,

    val artworkUrl100: String,

    val feedUrl: String
)
