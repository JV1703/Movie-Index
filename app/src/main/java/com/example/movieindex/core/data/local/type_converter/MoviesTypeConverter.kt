package com.example.movieindex.core.data.local.type_converter

import androidx.room.TypeConverter
import com.example.movieindex.core.common.extensions.fromJson
import com.google.gson.Gson

class MoviesTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun genreListToString(genreList: List<String>) = gson.toJson(genreList)

    @TypeConverter
    fun stringToGenreList(data: String) = gson.fromJson<List<String>>(data)

}