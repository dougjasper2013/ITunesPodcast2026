package com.trios2025dej.itunespodcast2026.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.data.SubscribedPodcast

class SubscriptionAdapter(
    private var items: List<SubscribedPodcast>,
    private val onPodcastClick:
        (SubscribedPodcast) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    fun updateList(
        newItems: List<SubscribedPodcast>
    ) {

        items = newItems

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_subscription,
                    parent,
                    false
                )

        return SubscriptionViewHolder(
            view,
            onPodcastClick
        )
    }

    override fun onBindViewHolder(
        holder: SubscriptionViewHolder,
        position: Int
    ) {

        holder.bind(
            items[position]
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class SubscriptionViewHolder(
        itemView: View,
        private val onPodcastClick:
            (SubscribedPodcast) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageArtwork: ImageView =
            itemView.findViewById(
                R.id.imageViewArtwork
            )

        private val textTitle: TextView =
            itemView.findViewById(
                R.id.textViewTitle
            )

        private val textArtist: TextView =
            itemView.findViewById(
                R.id.textViewArtist
            )

        fun bind(
            podcast: SubscribedPodcast
        ) {

            textTitle.text =
                podcast.collectionName

            textArtist.text =
                podcast.artistName

            Glide.with(itemView.context)
                .load(podcast.artworkUrl100)
                .into(imageArtwork)

            itemView.setOnClickListener {
                onPodcastClick(podcast)
            }
        }
    }
}