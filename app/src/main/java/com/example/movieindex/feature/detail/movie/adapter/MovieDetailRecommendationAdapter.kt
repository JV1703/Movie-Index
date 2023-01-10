package com.example.movieindex.feature.detail.movie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.common.extensions.loadErrorImage
import com.example.movieindex.core.common.extensions.loadImage
import com.example.movieindex.core.common.extensions.localeDateFormatter
import com.example.movieindex.core.common.extensions.toSuperScript
import com.example.movieindex.core.common.getCurrentLocale
import com.example.movieindex.core.common.getMovieRatingIndicatorColor
import com.example.movieindex.core.common.getMovieRatingTrackColor
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.core.data.remote.NetworkConstants
import com.example.movieindex.databinding.MovieDetailRecommendationViewMoreVhItemBinding
import com.example.movieindex.databinding.MovieVhItemBinding
import java.time.format.FormatStyle
import java.util.*

class MovieDetailRecommendationAdapter(
    private val onRecommendationClicked: (movieId: Int) -> Unit = {},
    private val onViewMoreClicked: () -> Unit = {},
) : ListAdapter<RvListHelper<Result>, RecyclerView.ViewHolder>(DiffUtilCallback) {

    class MovieViewHolder(private val binding: MovieVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Result) {

            val locale = getCurrentLocale(itemView.context)

            movie.posterPath?.let {
                binding.moviePoster.loadImage(NetworkConstants.BASE_IMG_URL + NetworkConstants.POSTER_SIZE_SMALL + it)
            } ?: binding.moviePoster.loadErrorImage()

            val movieRating = (movie.voteAverage?.times(10))?.toInt()
            binding.ratingsInd.apply {
                max = 100

                if (movieRating == null) {
                    progress = 0
                } else {
                    progress = movieRating

                    setIndicatorColor(ContextCompat.getColor(this.context, getMovieRatingIndicatorColor(movieRating)))
                    trackColor = ContextCompat.getColor(this.context, getMovieRatingTrackColor(movieRating))

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

    class MovieDetailRecommendationViewMoreViewHolder(private val binding: MovieDetailRecommendationViewMoreVhItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            1 -> {
                MovieViewHolder(MovieVhItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false))
            }
            2 -> {
                MovieDetailRecommendationViewMoreViewHolder(
                    MovieDetailRecommendationViewMoreVhItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false))
            }
            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentRecommendation = getItem(position)
        when (holder) {
            is MovieViewHolder -> {
                val recommendation =
                    (currentRecommendation as RvListHelper.DataWrapper)
                holder.bind(recommendation.data)
                holder.itemView.setOnClickListener {
                    onRecommendationClicked(recommendation.data.movieId)
                }
            }
            is MovieDetailRecommendationViewMoreViewHolder -> {
                holder.itemView.setOnClickListener { onViewMoreClicked() }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RvListHelper.DataWrapper -> {
                1
            }
            is RvListHelper.ViewMore -> {
                2
            }
        }
    }

    companion object DiffUtilCallback :
        DiffUtil.ItemCallback<RvListHelper<Result>>() {
        override fun areItemsTheSame(
            oldItem: RvListHelper<Result>,
            newItem: RvListHelper<Result>,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RvListHelper<Result>,
            newItem: RvListHelper<Result>,
        ): Boolean {
            return oldItem == newItem
        }
    }
}