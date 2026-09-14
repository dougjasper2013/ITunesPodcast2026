package com.trios2025dej.itunespodcast2026.ui

import android.os.Bundle
import android.text.Html
import android.view.View
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
import com.trios2025dej.itunespodcast2026.data.Episode
import com.trios2025dej.itunespodcast2026.data.PodcastRssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastDetailActivity : AppCompatActivity() {

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

    private lateinit var imageArtwork: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textArtist: TextView
    private lateinit var textDescription: TextView
    private lateinit var playerView: PlayerView
    private lateinit var recyclerViewEpisodes: RecyclerView

    private lateinit var episodeAdapter: EpisodeAdapter

    private var player: ExoPlayer? = null

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

        playerView =
            findViewById(
                R.id.playerView
            )

        recyclerViewEpisodes =
            findViewById(
                R.id.recyclerViewEpisodes
            )

        textTitle.text =
            intent.getStringExtra(
                EXTRA_TITLE
            ) ?: "Podcast"

        textArtist.text =
            intent.getStringExtra(
                EXTRA_ARTIST
            ) ?: ""

        val artworkUrl =
            intent.getStringExtra(
                EXTRA_ARTWORK
            )

        Glide.with(this)
            .load(artworkUrl)
            .into(imageArtwork)

        episodeAdapter =
            EpisodeAdapter(
                emptyList()
            ) { episode ->

                playEpisode(episode)
            }

        recyclerViewEpisodes.layoutManager =
            LinearLayoutManager(this)

        recyclerViewEpisodes.adapter =
            episodeAdapter

        initializePlayer()

        val feedUrl =
            intent.getStringExtra(
                EXTRA_FEED_URL
            )

        if (!feedUrl.isNullOrBlank()) {
            loadFeed(feedUrl)
        } else {
            textDescription.text =
                "No podcast feed was supplied."
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
                    withContext(Dispatchers.IO) {

                        PodcastRssParser
                            .parseFeed(feedUrl)
                    }

                if (episodes.isNotEmpty()) {

                    episodeAdapter.updateList(
                        episodes
                    )

                    val firstDescription =
                        episodes.first().description

                    textDescription.text =
                        cleanDescription(
                            firstDescription
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
                    .setUri(episode.mediaUrl)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(episode.title)
                            .build()
                    )

            val url =
                episode.mediaUrl.lowercase()

            if (
                episode.mediaType
                    .lowercase()
                    .contains("mpegurl") ||
                url.contains(".m3u8")
            ) {

                mediaItemBuilder.setMimeType(
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

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }

    override fun onStop() {

        super.onStop()

        player?.release()

        player = null
    }
}