package com.trios2025dej.itunespodcast2026.data

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trios2025dej.itunespodcast2026.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PodcastUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParams
) {

    companion object {

        const val ACTION_NEW_EPISODE =
            "com.trios2025dej.itunespodcast2026.NEW_EPISODE"

        const val EXTRA_PODCAST_TITLE =
            "podcast_title"

        const val EXTRA_EPISODE_TITLE =
            "episode_title"
    }

    override suspend fun doWork(): Result {

        return try {

            val database =
                AppDatabase.getDatabase(
                    applicationContext
                )

            val subscriptions =
                database
                    .subscriptionDao()
                    .getAll()
                    .first()

            for (
            podcast in subscriptions
            ) {

                checkPodcast(
                    podcast
                )
            }

            Result.success()

        } catch (
            e: Exception
        ) {

            e.printStackTrace()

            Result.retry()
        }
    }

    private suspend fun checkPodcast(
        podcast: SubscribedPodcast
    ) {

        val episodes =
            withContext(
                Dispatchers.IO
            ) {

                PodcastRssParser.parseFeed(
                    podcast.feedUrl
                )
            }

        if (episodes.isEmpty()) {
            return
        }

        val newestEpisode =
            episodes.first()

        val preferences =
            applicationContext.getSharedPreferences(
                "podcast_updates",
                Context.MODE_PRIVATE
            )

        val key =
            "latest_${podcast.trackId}"

        val previousGuid =
            preferences.getString(
                key,
                null
            )

        if (previousGuid == null) {

            // First check for this subscription.
            // Save the current newest episode without
            // generating a notification.
            preferences
                .edit()
                .putString(
                    key,
                    newestEpisode.guid
                )
                .apply()

            return
        }

        if (
            previousGuid !=
            newestEpisode.guid
        ) {

            preferences
                .edit()
                .putString(
                    key,
                    newestEpisode.guid
                )
                .apply()

            notifyNewEpisode(
                podcast,
                newestEpisode
            )
        }
    }

    private fun notifyNewEpisode(
        podcast: SubscribedPodcast,
        episode: Episode
    ) {

        val intent =
            Intent(
                ACTION_NEW_EPISODE
            ).apply {

                setPackage(
                    applicationContext.packageName
                )

                putExtra(
                    EXTRA_PODCAST_TITLE,
                    podcast.collectionName
                )

                putExtra(
                    EXTRA_EPISODE_TITLE,
                    episode.title
                )
            }

        /*
         * If MainActivity is currently visible,
         * its receiver will display a Toast.
         *
         * If the app is not running/visible,
         * the receiver is not registered and we
         * display a normal Android notification.
         */
        if (
            MainActivity.isInForeground
        ) {

            applicationContext.sendBroadcast(
                intent
            )

        } else {

            NotificationHelper
                .showEpisodeNotification(
                    applicationContext,
                    podcast,
                    episode
                )
        }
    }
}