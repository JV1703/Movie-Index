package com.exa

``````mple.movieindex.core.data .remote.model.details


import com.squareup.moshi.Json

data class ProductionCompanyResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "logo_path")
    val logoPath: String?,
    @Json(name = "name")
    val name: String,
    @Json(name = "origin_country")
    val originCountry: String,
)