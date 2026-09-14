package com.trios2025dej.itunespodcast2026.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.data.Podcast

class PodcastAdapter(
    private var items: List<Podcast>,
    private val onPodcastClick: (Podcast) -> Unit
) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>() {

    private val mediaTypes = mutableMapOf<Long, String>()

    fun updateList(newItems: List<Podcast>) {

        items = newItems

        mediaTypes.clear()

        notifyDataSetChanged()
    }

    fun setMediaType(
        trackId: Long,
        mediaType: String
    ) {

        mediaTypes[trackId] = mediaType

        val position = items.indexOfFirst {
            it.trackId == trackId
        }

        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_podcast,
                parent,
                false
            )

        return PodcastViewHolder(
            view,
            onPodcastClick
        )
    }

    override fun onBindViewHolder(
        holder: PodcastViewHolder,
        position: Int
    ) {

        val podcast = items[position]

        holder.bind(
            podcast,
            mediaTypes[podcast.trackId] ?: "Checking..."
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PodcastViewHolder(
        itemView: View,
        private val onPodcastClick: (Podcast) -> Unit
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

        private val textType: TextView =
            itemView.findViewById(
                R.id.textViewType
            )

        fun bind(
            podcast: Podcast,
            mediaType: String
        ) {

            textTitle.text =
                podcast.collectionName

            textArtist.text =
                podcast.artistName

            textType.text =
                mediaType

            Glide.with(itemView.context)
                .load(podcast.artworkUrl100)
                .into(imageArtwork)

            itemView.setOnClickListener {
                onPodcastClick(podcast)
            }
        }
    }
}