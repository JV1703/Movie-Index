package com.example.movieindex.feature.main.ui.search

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.example.movieindex.R
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.core.common.extensions.makeToast
import com.example.movieindex.core.data.external.model.Result
import com.example.movieindex.databinding.FragmentSearchBinding
import com.example.movieindex.feature.list.movie_list.PagingLoadStateAdapterType
import com.example.movieindex.feature.list.movie_list.adapter.MovieListAdapter
import com.example.movieindex.feature.list.movie_list.adapter.MovieListPagingLoadStateAdapter
import com.example.movieindex.feature.main.ui.MainFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var _rootNavController: NavController? = null
    private val rootNavController get() = _rootNavController!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var searchAdapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        _rootNavController = activity?.let { activity ->
            Navigation.findNavController(activity, R.id.root_nav_container)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("fragment lifecycle - onViewCreated")
        setupAdapter()

        binding.searchField.editText?.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.updateSearchQuery(textView.text.trim().toString())
                true
            }
            false
        }

        binding.retryButton.setOnClickListener {
            searchAdapter.retry()
        }

        collectLatestLifecycleFlow(viewModel.searchQuery) {
            binding.searchField.editText?.setText(it)
        }

        collectLatestLifecycleFlow(viewModel.searchResult) { pagingData: PagingData<Result> ->
            searchAdapter.submitData(pagingData)
        }

        collectLatestLifecycleFlow(searchAdapter.loadStateFlow) { loadState: CombinedLoadStates ->
            Timber.i("loadState: $loadState")
            if (loadState.refresh is LoadState.Loading) {
                binding.searchRv.scrollToPosition(0)
            }

            val isListEmpty =
                (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchAdapter.itemCount == 0)

            if (isListEmpty) {
                makeToast("No movie found")
            }

            binding.searchRv.isGone =
                loadState.refresh is LoadState.Error || loadState.refresh is LoadState.Loading && searchAdapter.itemCount != 0

            binding.loadingInd.isVisible =
                loadState.source.refresh is LoadState.Loading

            val errorState =
                loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                    ?: loadState.refresh as? LoadState.Error

            binding.retryButton.isGone = loadState.refresh !is LoadState.Error

            errorState?.let {
                makeToast("\uD83D\uDE28 Wooops ${it.error}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchRv.adapter = null
        _binding = null
    }

    private fun setupAdapter() {
        searchAdapter = MovieListAdapter {
            val action =
                MainFragmentDirections.actionMainFragmentToMovieDetailFragment(movieId = it)
            rootNavController.navigate(action)
        }.apply {
            stateRestorationPolicy = PREVENT_WHEN_EMPTY
            withLoadStateHeaderAndFooter(
                header = MovieListPagingLoadStateAdapter(type = PagingLoadStateAdapterType.Header,
                    retry = { this.retry() }
                ),
                footer = MovieListPagingLoadStateAdapter(type = PagingLoadStateAdapterType.Footer,
                    retry = { this.retry() }))
        }

        binding.searchRv.apply {
            adapter =
                searchAdapter.withLoadStateHeaderAndFooter(header = MovieListPagingLoadStateAdapter(
                    retry = { searchAdapter.retry() }),
                    footer = MovieListPagingLoadStateAdapter(
                        retry = { searchAdapter.retry() }))

            setHasFixedSize(true)

            addItemDecoration(object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State,
                ) {

                    if (parent.getChildAdapterPosition(view) == parent.adapter?.itemCount?.minus(1)) {

                        @Suppress("DEPRECATION")
                        val defaultDisplay = activity?.windowManager?.defaultDisplay

                        @Suppress("DEPRECATION")
                        val displayMetrics = resources.displayMetrics

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val windowMetrics =
                                requireContext().getSystemService(WindowManager::class.java).currentWindowMetrics
                            val height = windowMetrics.bounds.height()
                            outRect.bottom = (height * (1 - 0.915)).toInt()
                            Timber.i("height - S and Above $height")
                        }

                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                            defaultDisplay?.getRealMetrics(displayMetrics)
                            val height = displayMetrics.heightPixels
                            outRect.bottom = (height * (1 - 0.915)).toInt()
                            Timber.i("height - below R $height")
                        }


                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            @Suppress("DEPRECATION")
                            defaultDisplay?.getMetrics(displayMetrics)
                            val height = displayMetrics.heightPixels
                            outRect.bottom = (height * (1 - 0.915)).toInt()
                            Timber.i("height - below R $height")
                        }
                    }
                }
            })
        }
    }
}