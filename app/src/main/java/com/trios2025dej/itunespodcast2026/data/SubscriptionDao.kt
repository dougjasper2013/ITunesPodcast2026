package com.trios2025dej.itunespodcast2026.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        podcast: SubscribedPodcast
    )

    @Delete
    suspend fun delete(
        podcast: SubscribedPodcast
    )

    @Query(
        "SELECT * FROM subscribed_podcasts ORDER BY collectionName COLLATE NOCASE"
    )
    fun getAll(): Flow<List<SubscribedPodcast>>

    @Query(
        "SELECT * FROM subscribed_podcasts WHERE trackId = :trackId LIMIT 1"
    )
    suspend fun getByTrackId(
        trackId: Long
    ): SubscribedPodcast?

    @Query(
        "SELECT EXISTS(SELECT 1 FROM subscribed_podcasts WHERE trackId = :trackId)"
    )
    suspend fun isSubscribed(
        trackId: Long
    ): Boolean
}