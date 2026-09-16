package com.trios2025dej.itunespodcast2026.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PodcastUpdateScheduler {

    private const val PERIODIC_WORK_NAME =
        "podcast_update_check"

    private const val ONE_TIME_WORK_NAME =
        "podcast_initial_update_check"

    fun schedule(
        context: Context
    ) {

        val workManager =
            WorkManager.getInstance(
                context
            )

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(
                    NetworkType.CONNECTED
                )
                .build()

        /*
         * Periodic check.
         *
         * WorkManager minimum periodic interval
         * is 15 minutes.
         */
        val periodicRequest =
            PeriodicWorkRequestBuilder<
                    PodcastUpdateWorker
                    >(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(
                    constraints
                )
                .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        /*
         * Run one check shortly after the app
         * has been opened for the first time.
         */
        val oneTimeRequest =
            OneTimeWorkRequestBuilder<
                    PodcastUpdateWorker
                    >()
                .setConstraints(
                    constraints
                )
                .build()

        workManager.enqueueUniqueWork(
            ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeRequest
        )
    }
}