package com.example.movieindex.feature.main.ui.home

import androidx.lifecycle.ViewModel
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(private val movieUseCase: MovieUseCase) : ViewModel() {

    val nowPlaying = movieUseCase.getNowPlaying()
    val popularMovies = movieUseCase.getPopularMovies()
    val trendingMovies = movieUseCase.getTrendingMovies()

}