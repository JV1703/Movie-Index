package com.example.movieindex.feature.detail.movie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.extensions.loadImage
import com.example.movieindex.core.data.external.model.VideosResult
import com.example.movieindex.core.data.remote.NetworkConstants
import com.example.movieindex.databinding.MovieVideosVhItemBinding

class MovieVideosAdapter(private val onVideoClicked: (movieId: String) -> Unit) :
    ListAdapter<VideosResult, MovieVideosAdapter.MovieVideosViewHolder>(DiffUtilCallback) {

    class MovieVideosViewHolder(private val binding: MovieVideosVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: VideosResult) {
            val thumbnailPath = NetworkConstants.getYtThumbnail(video.key)
            binding.videoThumbnail.loadImage(thumbnailPath)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVideosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieVideosViewHolder(MovieVideosVhItemBinding.inflate(layoutInflater,
            parent,
            false))
    }

    override fun onBindViewHolder(holder: MovieVideosViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
        holder.itemView.setOnClickListener {
            onVideoClicked(video.key)
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<VideosResult>() {
        override fun areItemsTheSame(oldItem: VideosResult, newItem: VideosResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideosResult, newItem: VideosResult): Boolean {
            return oldItem == newItem
        }
    }
}