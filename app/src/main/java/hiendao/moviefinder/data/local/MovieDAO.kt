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

    @Query("UPDATE movie SET addedToFavorite = :addedToFavorite, addedInFavoriteDate = CASE WHEN :addedToFavorite == 1 THEN :date ELSE \"\" END WHERE id = :movieId")
    suspend fun changeFavorite(addedToFavorite: Int, date: String, movieId: Int)

//    SELECT * FROM TableName ORDER BY id OFFSET 10 ROWS FETCH NEXT 10 ROWS ONLY;
//    SELECT * FROM movie WHERE category = :category ORDER BY popularity DESC
    @Query("SELECT * FROM movie WHERE category LIKE :category ORDER BY popularity DESC LIMIT 20 OFFSET :number ")
    fun loadCategoryMovies(category: String, number: Int): List<MovieEntity>

    @Query("SELECT * FROM movie WHERE id = :movieId")
    fun getMovieWithId(movieId: Int): MovieEntity?

    @Query("SELECT * FROM movie WHERE id IN (:listIds)")
    fun getMoviesInListId(listIds: String): List<MovieEntity>

    @Query("DELETE FROM movie")
    fun deleteAllMovie()

    @Query("DELETE FROM movie WHERE category = :category")
    fun deleteAllMovieWithCategory(category: String)

    @Query("SELECT * FROM movie WHERE addedToFavorite = 1 ORDER BY addedInFavoriteDate DESC LIMIT 20 OFFSET :number")
    fun getFavoriteMovies(number: Int): List<MovieEntity>
}