package com.example.movieindex.feature.splash.domain

import androidx.lifecycle.ViewModel
import com.example.movieindex.feature.splash.domain.abstraction.SyncUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(syncUseCase: SyncUseCase) : ViewModel() {



}