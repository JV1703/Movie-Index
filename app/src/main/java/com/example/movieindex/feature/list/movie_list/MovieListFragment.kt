package com.example.movieindex.feature.list.movie_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.core.common.extensions.makeToast
import com.example.movieindex.databinding.FragmentMovieListBinding
import com.example.movieindex.feature.list.movie_list.adapter.MovieListAdapter
import com.example.movieindex.feature.list.movie_list.adapter.MovieListPagingLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private val viewModel: MovieListViewModel by viewModels()
    private val navArgs: MovieListFragmentArgs by navArgs()

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private var _listType: ListType? = null
    private val listType get() = _listType!!
    private var _movieId: Int? = null
    private val movieId get() = _movieId!!

    private lateinit var movieListAdapter: MovieListAdapter

    /* Unable to save lists in ViewModel saved state handle because the list is to large
    * try converting the list to string using gson, save it in DataStore Preference and retrieve it by calling DataStore Preference*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        _listType = navArgs.listType
        this._movieId = navArgs.recommendationMovieId
        viewModel.updateListType(listType)
        viewModel.saveMovieId(movieId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMovieListAdapter()

        binding.retryButton.setOnClickListener {
            movieListAdapter.retry()
        }

        collectLatestLifecycleFlow(viewModel.movieList) { data ->
            Timber.i("movieList - data:$data")
            movieListAdapter.submitData(data)
        }

        collectLatestLifecycleFlow(movieListAdapter.loadStateFlow) { loadState ->

            binding.prependProgress.isVisible = loadState.source.prepend is LoadState.Loading
            binding.appendProgress.isVisible = loadState.source.append is LoadState.Loading

            val isListEmpty =
                (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && movieListAdapter.itemCount == 0)

            if (isListEmpty) {
                binding.lottie.visibility = View.VISIBLE
                binding.movieListRv.visibility = View.GONE
            } else {
                binding.lottie.visibility = View.GONE
                binding.movieListRv.visibility = View.VISIBLE
            }

            binding.loadingInd.isVisible =
                loadState.source.refresh is LoadState.Loading

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error ?: loadState.prepend as? LoadState.Error
                ?: loadState.refresh as? LoadState.Error

            if (listType == ListType.Favorite || listType == ListType.Watchlist) {
                binding.movieListRv.isGone = isListEmpty
            } else {
                binding.retryButton.isGone = loadState.refresh !is LoadState.Error

                binding.movieListRv.isGone =
                    (loadState.refresh is LoadState.Error || loadState.refresh is LoadState.Loading && movieListAdapter.itemCount != 0)
            }

            errorState?.let {
                makeToast("\uD83D\uDE28 Wooops ${it.error}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMovieListAdapter() {
        movieListAdapter = MovieListAdapter { movieId ->
            val action =
                MovieListFragmentDirections.actionMovieListFragmentToMovieDetailFragment(movieId = movieId)
            findNavController().navigate(action)
        }.apply {
            stateRestorationPolicy = PREVENT_WHEN_EMPTY
        }

        binding.movieListRv.apply {
            adapter =
                movieListAdapter.withLoadStateHeaderAndFooter(header = MovieListPagingLoadStateAdapter(
                    retry = { movieListAdapter.retry() }),
                    footer = MovieListPagingLoadStateAdapter(
                        retry = { movieListAdapter.retry() }))
            setHasFixedSize(true)
        }
    }

}