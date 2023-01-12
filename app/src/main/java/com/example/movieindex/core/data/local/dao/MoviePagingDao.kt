package com.example.movieindex.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import com.example.movieindex.core.data.local.model.MoviePagingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviePagingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMovies(movies: List<MoviePagingEntity>)

    @Query("SELECT * FROM movie_paging_table WHERE pagingCategory = :pagingCategory")
    fun getMovies(pagingCategory: MoviePagingCategory): PagingSource<Int, MoviePagingEntity>

    @Query("SELECT * FROM movie_paging_table WHERE pagingCategory = :pagingCategory")
    fun getMoviesWithReferenceToPagingCategory(pagingCategory: MoviePagingCategory): Flow<List<MoviePagingEntity>>

    @Query("SELECT * FROM movie_paging_table")
    fun getAllMovies(): Flow<List<MoviePagingEntity>>

    @Query("DELETE FROM movie_paging_table WHERE pagingCategory = :pagingCategory")
    suspend fun clearMovies(pagingCategory: MoviePagingCategory)

}