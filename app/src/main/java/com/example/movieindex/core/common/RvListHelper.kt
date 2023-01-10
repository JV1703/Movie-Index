package com.example.movieindex.core.common

import com.example.movieindex.core.data.external.model.Cast
import com.example.movieindex.core.data.external.model.Crew

sealed class RvListHelper<out T> {
    data class DataWrapper<out T>(val data: T) : RvListHelper<T>()
    object ViewMore : RvListHelper<Nothing>()
}

sealed interface CastListHelper {
    data class CastDetails(val cast: Cast) : CastListHelper
    data class Header(val header: String, val headerDetails: Int) : CastListHelper
}

sealed interface CrewListHelper {
    data class CrewDetails(val crew: Crew) : CrewListHelper
    data class Header(val header: String, val headerDetails: Int) : CrewListHelper
    data class SubHeader(val subHeader: String) : CrewListHelper
}