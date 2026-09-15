package com.trios2025dej.itunespodcast2026.ui

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.data.AppDatabase
import com.trios2025dej.itunespodcast2026.data.Episode
import com.trios2025dej.itunespodcast2026.data.PodcastRssParser
import com.trios2025dej.itunespodcast2026.data.SubscribedPodcast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetailActivity :
    AppCompatActivity() {

    companion object {

        const val EXTRA_TRACK_ID =
            "track_id"

        const val EXTRA_TITLE =
            "title"

        const val EXTRA_ARTIST =
            "artist"

        const val EXTRA_ARTWORK =
            "artwork"

        const val EXTRA_FEED_URL =
            "feed_url"
    }

    private lateinit var imageArtwork:
            ImageView

    private lateinit var textTitle:
            TextView

    private lateinit var textArtist:
            TextView

    private lateinit var textDescription:
            TextView

    private lateinit var buttonSubscribe:
            Button

    private lateinit var playerView:
            PlayerView

    private lateinit var recyclerViewEpisodes:
            RecyclerView

    private lateinit var episodeAdapter:
            EpisodeAdapter

    private lateinit var database:
            AppDatabase

    private var player:
            ExoPlayer? = null

    private var isSubscribed =
        false

    private var podcastTrackId:
            Long = -1L

    private var podcastTitle =
        ""

    private var podcastArtist =
        ""

    private var podcastArtwork =
        ""

    private var podcastFeedUrl =
        ""

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_podcast_detail
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )

        imageArtwork =
            findViewById(
                R.id.imageViewPodcastArtwork
            )

        textTitle =
            findViewById(
                R.id.textViewPodcastTitle
            )

        textArtist =
            findViewById(
                R.id.textViewPodcastArtist
            )

        textDescription =
            findViewById(
                R.id.textViewDescription
            )

        buttonSubscribe =
            findViewById(
                R.id.buttonSubscribe
            )

        playerView =
            findViewById(
                R.id.playerView
            )

        recyclerViewEpisodes =
            findViewById(
                R.id.recyclerViewEpisodes
            )

        podcastTrackId =
            intent.getLongExtra(
                EXTRA_TRACK_ID,
                -1L
            )

        podcastTitle =
            intent.getStringExtra(
                EXTRA_TITLE
            ) ?: ""

        podcastArtist =
            intent.getStringExtra(
                EXTRA_ARTIST
            ) ?: ""

        podcastArtwork =
            intent.getStringExtra(
                EXTRA_ARTWORK
            ) ?: ""

        podcastFeedUrl =
            intent.getStringExtra(
                EXTRA_FEED_URL
            ) ?: ""

        textTitle.text =
            podcastTitle

        textArtist.text =
            podcastArtist

        Glide.with(this)
            .load(podcastArtwork)
            .into(imageArtwork)

        database =
            AppDatabase.getDatabase(this)

        buttonSubscribe.setOnClickListener {

            toggleSubscription()
        }

        episodeAdapter =
            EpisodeAdapter(
                emptyList()
            ) { episode ->

                playEpisode(
                    episode
                )
            }

        recyclerViewEpisodes.layoutManager =
            LinearLayoutManager(this)

        recyclerViewEpisodes.adapter =
            episodeAdapter

        initializePlayer()

        checkSubscriptionStatus()

        if (podcastFeedUrl.isNotBlank()) {
            loadFeed(
                podcastFeedUrl
            )
        } else {
            textDescription.text =
                "No podcast feed was supplied."
        }
    }

    private fun checkSubscriptionStatus() {

        lifecycleScope.launch {

            isSubscribed =
                withContext(Dispatchers.IO) {

                    database
                        .subscriptionDao()
                        .isSubscribed(
                            podcastTrackId
                        )
                }

            updateSubscriptionButton()
        }
    }

    private fun updateSubscriptionButton() {

        buttonSubscribe.text =
            if (isSubscribed) {
                "UNSUBSCRIBE"
            } else {
                "SUBSCRIBE"
            }
    }

    private fun toggleSubscription() {

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {

                if (!isSubscribed) {

                    val podcast =
                        SubscribedPodcast(
                            trackId =
                                podcastTrackId,

                            collectionName =
                                podcastTitle,

                            artistName =
                                podcastArtist,

                            artworkUrl100 =
                                podcastArtwork,

                            feedUrl =
                                podcastFeedUrl
                        )

                    database
                        .subscriptionDao()
                        .insert(podcast)

                } else {

                    val podcast =
                        SubscribedPodcast(
                            trackId =
                                podcastTrackId,

                            collectionName =
                                podcastTitle,

                            artistName =
                                podcastArtist,

                            artworkUrl100 =
                                podcastArtwork,

                            feedUrl =
                                podcastFeedUrl
                        )

                    database
                        .subscriptionDao()
                        .delete(podcast)
                }
            }

            isSubscribed =
                !isSubscribed

            updateSubscriptionButton()
        }
    }

    private fun initializePlayer() {

        player =
            ExoPlayer.Builder(this)
                .build()

        playerView.player =
            player
    }

    private fun loadFeed(
        feedUrl: String
    ) {

        lifecycleScope.launch {

            try {

                val episodes =
                    withContext(
                        Dispatchers.IO
                    ) {

                        PodcastRssParser
                            .parseFeed(
                                feedUrl
                            )
                    }

                if (episodes.isNotEmpty()) {

                    episodeAdapter.updateList(
                        episodes
                    )

                    val description =
                        episodes
                            .first()
                            .description

                    textDescription.text =
                        cleanDescription(
                            description
                        )

                } else {

                    textDescription.text =
                        "No episodes were found."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                textDescription.text =
                    "Unable to load the podcast feed."
            }
        }
    }

    private fun playEpisode(
        episode: Episode
    ) {

        val currentPlayer =
            player ?: return

        try {

            val mediaItemBuilder =
                MediaItem.Builder()
                    .setUri(
                        episode.mediaUrl
                    )
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(
                                episode.title
                            )
                            .build()
                    )

            val url =
                episode.mediaUrl
                    .lowercase()

            if (
                episode.mediaType
                    .lowercase()
                    .contains("mpegurl") ||
                url.contains(".m3u8")
            ) {

                mediaItemBuilder
                    .setMimeType(
                        MimeTypes.APPLICATION_M3U8
                    )
            }

            val mediaItem =
                mediaItemBuilder.build()

            currentPlayer.setMediaItem(
                mediaItem
            )

            currentPlayer.prepare()

            currentPlayer.play()

            playerView.visibility =
                View.VISIBLE

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun cleanDescription(
        description: String
    ): String {

        if (description.isBlank()) {
            return "No description available."
        }

        return Html.fromHtml(
            description,
            Html.FROM_HTML_MODE_LEGACY
        )
            .toString()
            .trim()
    }

    override fun onSupportNavigateUp():
            Boolean {

        finish()

        return true
    }

    override fun onStop() {

        super.onStop()

        player?.release()

        player = null
    }
}