package com.example.movieindex.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieindex.core.data.local.dao.AccountDao
import com.example.movieindex.core.data.local.dao.MoviePagingDao
import com.example.movieindex.core.data.local.dao.MoviePagingKeyDao
import com.example.movieindex.core.data.local.model.AccountEntity
import com.example.movieindex.core.data.local.model.MovieEntityKey
import com.example.movieindex.core.data.local.model.MoviePagingEntity
import com.example.movieindex.core.data.local.type_converter.MoviesTypeConverter

@Database(entities = [MoviePagingEntity::class, MovieEntityKey::class, AccountEntity::class],
    version = 1)
@TypeConverters(MoviesTypeConverter::class)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun moviePagingDao(): MoviePagingDao
    abstract fun moviePagingKeyDao(): MoviePagingKeyDao
    abstract fun accountDao(): AccountDao
}