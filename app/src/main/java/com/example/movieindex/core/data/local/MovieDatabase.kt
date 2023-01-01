package com.example.movieindex.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.dao.MovieKeyDao
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.type_converter.MoviesTypeConverter

@Database(entities = [MovieEntity::class, MovieKeyEntity::class], version = 1)
@TypeConverters(MoviesTypeConverter::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun movieKeyDao(): MovieKeyDao
}