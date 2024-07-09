package hiendao.moviefinder.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import hiendao.moviefinder.data.local.model.MovieEntity

@Dao
interface MovieDAO {

    @Upsert
    suspend fun upsertListMovie(movies: List<MovieEntity>)

    @Upsert
    suspend fun upsertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movie")
    fun loadAllPaging(): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movie WHERE category = :category")
    fun loadCategoryMoviesPaged(category: String): PagingSource<Int, MovieEntity>

    @Query("SELECT * FROM movie WHERE category = :category")
    fun loadCategoryMovies(category: String): List<MovieEntity>

    @Query("SELECT * FROM movie WHERE id = :movieId")
    fun getMovieWithId(movieId: Int): MovieEntity?

    @Query("DELETE FROM movie")
    fun deleteAllMovie()

    @Query("DELETE FROM movie WHERE category = :category")
    fun deleteAllMovieWithCategory(category: String)
}