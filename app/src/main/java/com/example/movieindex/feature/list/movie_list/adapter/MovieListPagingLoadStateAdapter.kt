package com.example.movieindex.feature.list.movie_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movieindex.databinding.PagingLoadStateVhBinding
import com.example.movieindex.feature.list.movie_list.PagingLoadStateAdapterType

class MovieListPagingLoadStateAdapter(
    private val type: PagingLoadStateAdapterType = PagingLoadStateAdapterType.Header,
    private val retry: () -> Unit,
) : LoadStateAdapter<MovieListPagingLoadStateAdapter.MovieListPagingLoadStateViewHolder>() {

    inner class MovieListPagingLoadStateViewHolder(private val binding: PagingLoadStateVhBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
            binding.retryButton.setOnClickListener { retry.invoke() }

            binding.bottomPadding.isGone = type == PagingLoadStateAdapterType.Header
        }
    }

    override fun onBindViewHolder(
        holder: MovieListPagingLoadStateViewHolder,
        loadState: LoadState,
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): MovieListPagingLoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieListPagingLoadStateViewHolder(
            PagingLoadStateVhBinding.inflate(
                layoutInflater, parent, false
            )
        )
    }

}