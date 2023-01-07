package com.example.movieindex.feature.main.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.movieindex.core.common.extensions.loadErrorImage
import com.example.movieindex.core.common.extensions.loadImage
import com.example.movieindex.core.data.external.Result
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_IMG_URL
import com.example.movieindex.core.data.remote.NetworkConstants.POSTER_SIZE_LARGE
import com.example.movieindex.databinding.NowPlayingVhItemBinding

class NowPlayingAdapter(private val navigateOnClick: (movieId: Int) -> Unit) :
    ListAdapter<Result, NowPlayingAdapter.MovieViewHolder>(DiffUtilCallback) {

    class MovieViewHolder(private val binding: NowPlayingVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Result) {
            movie.posterPath?.let {
                binding.moviePoster.loadImage(
                    source = BASE_IMG_URL + POSTER_SIZE_LARGE + movie.posterPath
                )
            } ?: binding.moviePoster.loadErrorImage()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieViewHolder(NowPlayingVhItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = getItem(position)
        holder.bind(currentMovie)
        holder.itemView.setOnClickListener {
            navigateOnClick(currentMovie.movieId)
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }

    fun circularProgressDrawable(context: Context): CircularProgressDrawable {
        val loadingInd = CircularProgressDrawable(context)
        loadingInd.centerRadius = 30f
        loadingInd.strokeWidth = 5f
        return loadingInd
    }
}