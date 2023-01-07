package com.example.movieindex.feature.list.movie_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.extensions.loadImage
import com.example.movieindex.core.data.external.Result
import com.example.movieindex.core.data.remote.NetworkConstants
import com.example.movieindex.databinding.MovieListVhItemBinding

class MovieListAdapter(private val onMovieClicked: (movieId: Int) -> Unit) :
    PagingDataAdapter<Result, MovieListAdapter.MovieListViewHolder>(DiffUtilCallback) {

    class MovieListViewHolder(private val binding: MovieListVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Result) {
            binding.moviePoster.loadImage(NetworkConstants.BASE_IMG_URL + NetworkConstants.POSTER_SIZE_SMALL + movie.posterPath)
            binding.movieTitle.text = movie.title
            binding.releaseDate.text = movie.releaseDate
            binding.overview.text = movie.overview
        }
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {
        val currentMovie = getItem(position)
        currentMovie?.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onMovieClicked(currentMovie.movieId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieListViewHolder(MovieListVhItemBinding.inflate(layoutInflater, parent, false))
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.movieId == newItem.movieId
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem == newItem
        }
    }
}