package com.example.movieindex.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieindex.core.data.local.model.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies_table WHERE movieId = :movieId")
    fun getMovie(movieId: Int): Flow<MovieEntity?>

    @Query("SELECT * FROM movies_table WHERE isFavorite = 1")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT COUNT(*) FROM movies_table WHERE isFavorite = 1")
    fun getFavoriteCount(): Flow<Int>

    @Query("SELECT * FROM movies_table WHERE isBookmark = 1")
    fun getBookmarkedMovies(): Flow<List<MovieEntity>>
    @Query("SELECT COUNT(*) FROM movies_table WHERE isBookmark = 1")
    fun getWatchlistCount(): Flow<Int>

    @Query("UPDATE movies_table SET isBookmark = :isBookmark WHERE movieId = :movieId")
    suspend fun updateBookmark(movieId: Int, isBookmark: Boolean)

    @Query("UPDATE movies_table SET isFavorite = :isFavorite WHERE movieId = :movieId")
    suspend fun updateFavorite(movieId: Int, isFavorite: Boolean)

    @Query("DELETE FROM movies_table WHERE movieId = :movieId")
    suspend fun deleteMovie(movieId: Int)

}