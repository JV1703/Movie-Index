package com.example.movieindex.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieindex.core.data.local.model.MovieKeyEntity
import com.example.movieindex.core.data.local.model.MoviePagingCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMovieKeys(movieKeys: List<MovieKeyEntity>)

    @Query("SELECT * FROM movie_key_table")
    fun getAllMovieKey(): Flow<List<MovieKeyEntity>>

    @Query("SELECT * FROM movie_key_table WHERE id = :id AND pagingCategory = :pagingCategory")
    suspend fun movieKeyId(id: String, pagingCategory: MoviePagingCategory): MovieKeyEntity?

    @Query("DELETE FROM movie_key_table WHERE pagingCategory = :pagingCategory")
    suspend fun clearMovieKeys(pagingCategory: MoviePagingCategory)

}