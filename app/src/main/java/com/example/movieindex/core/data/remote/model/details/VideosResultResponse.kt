package com.example.movieindex.core.data.remote.model.details


import com.example.movieindex.core.data.external.VideosResult
import com.squareup.moshi.Json

data class VideosResultResponse(
    @Json(name = "id") val id: String,
    @Json(name = "iso_3166_1") val iso31661: String?,
    @Json(name = "iso_639_1") val iso6391: String?,
    @Json(name = "key") val key: String,
    @Json(name = "name") val name: String?,
    @Json(name = "official") val official: Boolean?,
    @Json(name = "published_at") val publishedAt: String?,
    @Json(name = "site") val site: String?,
    @Json(name = "size") val size: Int?,
    @Json(name = "type") val type: String?,
)

fun VideosResultResponse.toVideosResult() = VideosResult(
    id = id,
    iso31661 = iso31661,
    iso6391 = iso6391,
    key = key,
    name = name,
    official = official,
    publishedAt = publishedAt,
    site = site,
    size = size,
    type = type
)