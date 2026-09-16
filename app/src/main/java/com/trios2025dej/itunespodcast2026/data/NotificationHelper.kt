package com.trios2025dej.itunespodcast2026.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.ui.PodcastDetailActivity

object NotificationHelper {

    const val CHANNEL_ID =
        "podcast_updates"

    private const val CHANNEL_NAME =
        "Podcast Updates"

    private const val CHANNEL_DESCRIPTION =
        "Notifications when subscribed podcasts publish new episodes"

    fun createNotificationChannel(
        context: Context
    ) {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            channel.description =
                CHANNEL_DESCRIPTION

            val manager =
                context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }

    fun showEpisodeNotification(
        context: Context,
        podcast: SubscribedPodcast,
        episode: Episode
    ) {

        if (
            android.os.Build.VERSION.SDK_INT >= 33
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
        }

        createNotificationChannel(
            context
        )

        val intent =
            Intent(
                context,
                PodcastDetailActivity::class.java
            ).apply {

                putExtra(
                    PodcastDetailActivity.EXTRA_TRACK_ID,
                    podcast.trackId
                )

                putExtra(
                    PodcastDetailActivity.EXTRA_TITLE,
                    podcast.collectionName
                )

                putExtra(
                    PodcastDetailActivity.EXTRA_ARTIST,
                    podcast.artistName
                )

                putExtra(
                    PodcastDetailActivity.EXTRA_ARTWORK,
                    podcast.artworkUrl100
                )

                putExtra(
                    PodcastDetailActivity.EXTRA_FEED_URL,
                    podcast.feedUrl
                )

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                podcast.trackId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    android.R.drawable.ic_media_play
                )
                .setContentTitle(
                    podcast.collectionName
                )
                .setContentText(
                    "New episode: ${episode.title}"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "New episode available:\n${episode.title}"
                        )
                )
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(true)
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(
                podcast.trackId.toInt(),
                notification
            )
    }
}