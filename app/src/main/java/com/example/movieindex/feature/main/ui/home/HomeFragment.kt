package com.example.movieindex.feature.main.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.example.movieindex.R
import com.example.movieindex.core.common.ColorPalette
import com.example.movieindex.core.common.extensions.collectLatestLifecycleFlow
import com.example.movieindex.core.data.external.Resource
import com.example.movieindex.databinding.FragmentHomeBinding
import com.example.movieindex.feature.list.movie_list.ListType
import com.example.movieindex.feature.main.ui.MainFragmentDirections
import com.example.movieindex.feature.main.ui.home.adapter.MoviesCardAdapter
import com.example.movieindex.feature.main.ui.home.adapter.NowPlayingAdapter
import com.example.movieindex.feature.main.ui.home.adapter.pageTransformer.SliderPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var colorPalette: ColorPalette

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var _rootNavController: NavController? = null
    private val rootNavController get() = _rootNavController!!

    private val viewModel: MovieViewModel by viewModels()
    private lateinit var nowPlayingAdapter: NowPlayingAdapter
    private lateinit var trendingMoviesAdapter: MoviesCardAdapter
    private lateinit var popularMoviesAdapter: MoviesCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _rootNavController = activity?.let { activity ->
            Navigation.findNavController(activity, R.id.root_nav_container)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNowPlayingAdapter()
        setupPopularMoviesAdapter()
        setupTrendingMoviesAdapter()

        binding.viewMoreNowPlayingMovies.setOnClickListener { navigateToMovieList(listType = ListType.NowPlaying) }
        binding.viewMorePopularMovies.setOnClickListener { navigateToMovieList(listType = ListType.Popular) }
        binding.viewMoreTrendingMovies.setOnClickListener { navigateToMovieList(listType = ListType.Trending) }

        collectLatestLifecycleFlow(viewModel.nowPlaying) {
            if (it is Resource.Success) {
                nowPlayingAdapter.submitList(it.data)
            }
        }

        collectLatestLifecycleFlow(viewModel.popularMovies) {
            if (it is Resource.Success) {
                popularMoviesAdapter.submitList(it.data)
            }
        }

        collectLatestLifecycleFlow(viewModel.trendingMovies) {
            if (it is Resource.Success) {
                trendingMoviesAdapter.submitList(it.data)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.nowPlayingVp.adapter = null
        binding.popularMoviesRv.adapter = null
        binding.trendingMoviesRv.adapter = null
        _binding = null
    }

    private fun setupNowPlayingAdapter() {
        nowPlayingAdapter = NowPlayingAdapter { movieId ->
            val action =
                MainFragmentDirections.actionMainFragmentToMovieDetailFragment(movieId = movieId)
            rootNavController.navigate(action)
        }
        nowPlayingAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.nowPlayingVp.apply {
            val offscreenLimit = 3
            adapter = nowPlayingAdapter
            offscreenPageLimit = offscreenLimit
            setPageTransformer(SliderPageTransformer(offscreenLimit))
        }
    }

    private fun setupPopularMoviesAdapter() {
        popularMoviesAdapter = MoviesCardAdapter { movieId ->
            val action =
                MainFragmentDirections.actionMainFragmentToMovieDetailFragment(movieId = movieId)
            rootNavController.navigate(action)
        }
        popularMoviesAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.popularMoviesRv.apply {
            adapter = popularMoviesAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupTrendingMoviesAdapter() {
        trendingMoviesAdapter = MoviesCardAdapter { movieId ->
            val action =
                MainFragmentDirections.actionMainFragmentToMovieDetailFragment(movieId = movieId)
            rootNavController.navigate(action)
        }

        trendingMoviesAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.trendingMoviesRv.apply {
            adapter = trendingMoviesAdapter
            setHasFixedSize(true)
        }
    }

    private fun navigateToMovieList(listType: ListType) {
        val action =
            MainFragmentDirections.actionMainFragmentToMovieListFragment(listType = listType)
        rootNavController.navigate(action)
    }
}