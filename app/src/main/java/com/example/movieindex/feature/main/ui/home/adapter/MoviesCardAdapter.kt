package com.example.movieindex.feature.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.extensions.loadErrorImage
import com.example.movieindex.core.common.extensions.loadImage
import com.example.movieindex.core.common.extensions.localeDateFormatter
import com.example.movieindex.core.common.extensions.toSuperScript
import com.example.movieindex.core.common.getCurrentLocale
import com.example.movieindex.core.common.getMovieRatingIndicatorColor
import com.example.movieindex.core.common.getMovieRatingTrackColor
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_IMG_URL
import com.example.movieindex.core.data.remote.NetworkConstants.POSTER_SIZE_SMALL
import com.example.movieindex.databinding.MovieVhItemBinding
import java.time.format.FormatStyle
import java.util.*

class MoviesCardAdapter(private val onMovieClicked: (movieId: Int) -> Unit) :
    ListAdapter<Result, MoviesCardAdapter.MovieViewHolder>(DiffUtilCallback) {

    class MovieViewHolder(private val binding: MovieVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Result) {

            val locale = getCurrentLocale(itemView.context)

            movie.posterPath?.let {
                binding.moviePoster.loadImage(BASE_IMG_URL + POSTER_SIZE_SMALL + it)
            } ?: binding.moviePoster.loadErrorImage()

            val movieRating = (movie.voteAverage?.times(10))?.toInt()
            binding.ratingsInd.apply {
                max = 100
                if (movieRating == null) {
                    progress = 0
                } else {
                    progress = movieRating

                    setIndicatorColor(ContextCompat.getColor(this.context,
                        getMovieRatingIndicatorColor(movieRating)))
                    trackColor =
                        ContextCompat.getColor(this.context, getMovieRatingTrackColor(movieRating))

                }
            }

            binding.ratingsTv.apply {
                if (movieRating == null) {
                    text = "NR"
                } else {
                    text = "${movieRating}%"
                    toSuperScript(startIndex = text.indexOf('%'), endIndex = text.length)
                }
            }
            binding.movieTitle.text = movie.title
            binding.movieReleaseDate.text =
                if (!movie.releaseDate.isNullOrEmpty()) {
                    movie.releaseDate.localeDateFormatter(inputFormat = "yyyy-MM-dd",
                        outputFormat = FormatStyle.SHORT,
                        targetLocale = locale ?: Locale.getDefault())
                } else {
                    ""
                }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieViewHolder(MovieVhItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentMovie = getItem(position)
        holder.bind(currentMovie)
        holder.itemView.setOnClickListener {
            onMovieClicked(currentMovie.movieId)
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
}