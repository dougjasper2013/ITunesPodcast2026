package com.trios2025dej.itunespodcast2026.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.data.ITunesApi
import com.trios2025dej.itunespodcast2026.data.Podcast
import com.trios2025dej.itunespodcast2026.data.PodcastRssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity :
    AppCompatActivity() {

    private lateinit var editTextSearch:
            EditText

    private lateinit var buttonSearch:
            Button

    private lateinit var buttonSubscriptions:
            Button

    private lateinit var recyclerView:
            RecyclerView

    private lateinit var adapter:
            PodcastAdapter

    private lateinit var api:
            ITunesApi

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_main
        )

        editTextSearch =
            findViewById(
                R.id.editTextSearch
            )

        buttonSearch =
            findViewById(
                R.id.buttonSearch
            )

        buttonSubscriptions =
            findViewById(
                R.id.buttonSubscriptions
            )

        recyclerView =
            findViewById(
                R.id.recyclerView
            )

        adapter =
            PodcastAdapter(
                emptyList()
            ) { podcast ->

                openPodcast(
                    podcast
                )
            }

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter

        val retrofit =
            Retrofit.Builder()
                .baseUrl(
                    "https://itunes.apple.com/"
                )
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()

        api =
            retrofit.create(
                ITunesApi::class.java
            )

        buttonSearch.setOnClickListener {

            val query =
                editTextSearch.text
                    .toString()
                    .trim()

            if (query.isNotEmpty()) {

                searchPodcasts(query)
            }
        }

        buttonSubscriptions.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SubscriptionsActivity::class.java
                )
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    private fun openPodcast(
        podcast: Podcast
    ) {

        val intent =
            Intent(
                this,
                PodcastDetailActivity::class.java
            )

        intent.putExtra(
            PodcastDetailActivity.EXTRA_TRACK_ID,
            podcast.trackId
        )

        intent.putExtra(
            PodcastDetailActivity.EXTRA_TITLE,
            podcast.collectionName
        )

        intent.putExtra(
            PodcastDetailActivity.EXTRA_ARTIST,
            podcast.artistName
        )

        intent.putExtra(
            PodcastDetailActivity.EXTRA_ARTWORK,
            podcast.artworkUrl100
        )

        intent.putExtra(
            PodcastDetailActivity.EXTRA_FEED_URL,
            podcast.feedUrl
        )

        startActivity(intent)
    }

    private fun searchPodcasts(
        query: String
    ) {

        lifecycleScope.launch {

            try {

                val response =
                    api.searchPodcasts(query)

                adapter.updateList(
                    response.results
                )

                classifyPodcastTypes(
                    response.results
                )

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    private fun classifyPodcastTypes(
        podcasts: List<Podcast>
    ) {

        podcasts.forEach { podcast ->

            lifecycleScope.launch {

                val type =
                    withContext(
                        Dispatchers.IO
                    ) {

                        try {

                            PodcastRssParser
                                .determinePodcastType(
                                    podcast.feedUrl
                                )

                        } catch (
                            e: Exception
                        ) {

                            "Unknown"
                        }
                    }

                adapter.setMediaType(
                    podcast.trackId,
                    type
                )
            }
        }
    }
}