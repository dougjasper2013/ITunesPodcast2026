package com.trios2025dej.itunespodcast2026.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trios2025dej.itunespodcast2026.R
import com.trios2025dej.itunespodcast2026.data.Episode

class EpisodeAdapter(
    private var episodes: List<Episode>,
    private val onEpisodeClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    fun updateList(
        newEpisodes: List<Episode>
    ) {

        episodes = newEpisodes

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_episode,
                parent,
                false
            )

        return EpisodeViewHolder(
            view,
            onEpisodeClick
        )
    }

    override fun onBindViewHolder(
        holder: EpisodeViewHolder,
        position: Int
    ) {

        holder.bind(
            episodes[position]
        )
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    class EpisodeViewHolder(
        itemView: View,
        private val onEpisodeClick: (Episode) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textTitle: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeTitle
            )

        private val textType: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeType
            )

        private val textDate: TextView =
            itemView.findViewById(
                R.id.textViewEpisodeDate
            )

        fun bind(
            episode: Episode
        ) {

            textTitle.text =
                episode.title

            textType.text =
                if (episode.isVideo) {
                    "VIDEO"
                } else {
                    "AUDIO"
                }

            textDate.text =
                episode.pubDate

            itemView.setOnClickListener {
                onEpisodeClick(episode)
            }
        }
    }
}