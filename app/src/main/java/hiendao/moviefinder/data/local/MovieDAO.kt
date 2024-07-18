package hiendao.moviefinder.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import hiendao.moviefinder.data.local.model.MovieEntity

@Dao
interface MovieDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListMovie(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("UPDATE movie SET credits = :credit WHERE id = :movieId")
    suspend fun updateCredits(credit: String, movieId: Int)

    @Query("UPDATE movie SET addedToFavorite = :addedToFavorite WHERE id = :movieId")
    suspend fun changeFavorite(addedToFavorite: Int, movieId: Int)

    @Query("SELECT * FROM movie")
    fun loadAllPaging(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movie WHERE category = :category ORDER BY popularity DESC")
    fun loadCategoryMoviesPaged(category: String): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movie WHERE category = :category ORDER BY popularity DESC")
    fun loadCategoryMovies(category: String): List<MovieEntity>

    @Query("SELECT * FROM movie WHERE id = :movieId")
    fun getMovieWithId(movieId: Int): MovieEntity?

    @Query("SELECT * FROM movie WHERE id IN (:listIds)")
    fun getMoviesInListId(listIds: String): List<MovieEntity>

    @Query("DELETE FROM movie")
    fun deleteAllMovie()

    @Query("DELETE FROM movie WHERE category = :category")
    fun deleteAllMovieWithCategory(category: String)
}