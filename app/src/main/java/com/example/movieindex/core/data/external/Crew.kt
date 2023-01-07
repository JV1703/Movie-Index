package com.example.movieindex.core.data.external

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Crew(
    val adult: Boolean,
    val creditId: String,
    val department: String,
    val gender: Int,
    val id: Int,
    val job: String,
    val knownForDepartment: String,
    val name: String,
    val originalName: String,
    val popularity: Double,
    val profilePath: String?,
) : Parcelable