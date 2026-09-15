package com.trios2025dej.itunespodcast2026.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.data.AppDatabase
import com.trios2025dej.itunespodcast2026.data.SubscribedPodcast
import kotlinx.coroutines.launch

class SubscriptionsActivity : AppCompatActivity() {

    private lateinit var recyclerView:
            RecyclerView

    private lateinit var textViewNoSubscriptions:
            TextView

    private lateinit var adapter:
            SubscriptionAdapter

    private lateinit var database:
            AppDatabase

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_subscriptions
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )

        recyclerView =
            findViewById(
                R.id.recyclerViewSubscriptions
            )

        textViewNoSubscriptions =
            findViewById(
                R.id.textViewNoSubscriptions
            )

        database =
            AppDatabase.getDatabase(this)

        adapter =
            SubscriptionAdapter(
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

        observeSubscriptions()
    }

    private fun observeSubscriptions() {

        lifecycleScope.launch {

            database
                .subscriptionDao()
                .getAll()
                .collect { subscriptions ->

                    adapter.updateList(
                        subscriptions
                    )

                    if (subscriptions.isEmpty()) {

                        textViewNoSubscriptions
                            .visibility =
                            TextView.VISIBLE

                        recyclerView.visibility =
                            RecyclerView.GONE

                    } else {

                        textViewNoSubscriptions
                            .visibility =
                            TextView.GONE

                        recyclerView.visibility =
                            RecyclerView.VISIBLE
                    }
                }
        }
    }

    private fun openPodcast(
        podcast: SubscribedPodcast
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

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}