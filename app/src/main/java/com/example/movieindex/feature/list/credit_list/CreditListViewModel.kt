package com.example.movieindex.feature.list.credit_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieindex.core.common.CastListHelper
import com.example.movieindex.core.common.CrewListHelper
import com.example.movieindex.feature.common.domain.abstraction.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreditListViewModel @Inject constructor(
    private val movieUseCase: MovieUseCase,
) :
    ViewModel() {

    private val casts = movieUseCase.getCasts().map { casts ->
        val output = arrayListOf<CastListHelper>()
        if (casts.isNotEmpty()) {
            Timber.i("casts: $casts")
            output.add(CastListHelper.Header("Casts", casts.size))
            casts.forEach {
                output.add(CastListHelper.CastDetails(it))
            }
        }
        output
    }.catch { t -> Timber.e("casts: ${t.message}") }.flowOn(Dispatchers.Default)

    private val crews = movieUseCase.getCrews().map { crews ->
        val output = arrayListOf<CrewListHelper>()
        if (crews.isNotEmpty()) {
            Timber.i("crews: $crews")
            output.add(CrewListHelper.Header("Crews", crews.size))
            crews.map { it.department }
                .distinct()
                .sortedBy { department -> department }
                .forEach { department ->
                    output.add(CrewListHelper.SubHeader(department))
                    crews
                        .filter { crew -> crew.department == department }
                        .sortedWith(compareBy({ crew -> crew.job }, { crew -> crew.name }))
                        .forEach { crew ->
                            output.add(CrewListHelper.CrewDetails(crew))
                        }
                }
        }
        output
    }.catch { t -> Timber.e("crews: ${t.message}") }.flowOn(Dispatchers.Default)

    val credits =
        combine(casts, crews) { casts, crews ->
            Credits(casts = casts, crews = crews)
        }.stateIn(scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Credits())

    data class Credits(
        val casts: List<CastListHelper> = emptyList(),
        val crews: List<CrewListHelper> = emptyList(),
    )


}