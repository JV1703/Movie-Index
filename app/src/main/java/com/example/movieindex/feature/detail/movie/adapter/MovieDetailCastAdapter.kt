package com.example.movieindex.feature.detail.movie.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.core.common.RvListHelper
import com.example.movieindex.core.common.extensions.loadErrorImage
import com.example.movieindex.core.common.extensions.loadImage
import com.example.movieindex.core.data.external.model.Cast
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_IMG_URL
import com.example.movieindex.core.data.remote.NetworkConstants.CREDIT_IMG_SIZE
import com.example.movieindex.databinding.MovieDetailCastVhItemBinding
import com.example.movieindex.databinding.MovieDetailCastViewMoreVhItemBinding

class MovieDetailCastAdapter(
    private val onCastClicked: (id: Int) -> Unit = {},
    private val onViewMoreClicked: () -> Unit = {},
) :
    ListAdapter<RvListHelper<Cast>, RecyclerView.ViewHolder>(DiffUtilCallback) {

    class MovieDetailCastViewHolder(private val binding: MovieDetailCastVhItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cast: RvListHelper.DataWrapper<Cast>) {

            cast.data.profilePath?.let {
                binding.castImg.loadImage(BASE_IMG_URL + CREDIT_IMG_SIZE + it)
            } ?: binding.castImg.loadErrorImage()

            binding.castName.text = cast.data.name
            binding.characterName.text = cast.data.character
        }
    }

    class MovieDetailCastViewMoreViewHolder(private val binding: MovieDetailCastViewMoreVhItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            1 -> {
                MovieDetailCastViewHolder(MovieDetailCastVhItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false))
            }
            2 -> {
                MovieDetailCastViewMoreViewHolder(MovieDetailCastViewMoreVhItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false))
            }
            else -> throw IllegalArgumentException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentCast = getItem(position)
        when (holder) {
            is MovieDetailCastViewHolder -> {
                val cast = (currentCast as RvListHelper.DataWrapper<Cast>)
                holder.bind(cast)
                holder.itemView.setOnClickListener {
                    onCastClicked(cast.data.id)
                }
            }
            is MovieDetailCastViewMoreViewHolder -> {
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
        DiffUtil.ItemCallback<RvListHelper<Cast>>() {
        override fun areItemsTheSame(
            oldItem: RvListHelper<Cast>,
            newItem: RvListHelper<Cast>,
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RvListHelper<Cast>,
            newItem: RvListHelper<Cast>,
        ): Boolean {
            return oldItem == newItem
        }
    }

}