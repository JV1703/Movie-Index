package com.example.movieindex.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieindex.core.data.local.model.MovieEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMovies(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies_table WHERE pagingCategory = :pagingCategory")
    fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movies_table WHERE pagingCategory = :pagingCategory")
    fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies_table")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("DELETE FROM movies_table WHERE pagingCategory = :pagingCategory")
    suspend fun clearMovies(pagingCategory: MoviePagingCategory)

    @Query("SELECT * FROM movies_table")
    fun getMovies(): Flow<List<MovieEntity>>

}