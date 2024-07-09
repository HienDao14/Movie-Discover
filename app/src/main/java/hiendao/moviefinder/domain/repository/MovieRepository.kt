package hiendao.moviefinder.domain.repository

import androidx.paging.PagingData
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getPopularMoviesPaged(): Flow<PagingData<MovieEntity>>

    suspend fun getTopRatedMovies(): Flow<PagingData<MovieEntity>>

    suspend fun getNowPlayingMovies(): Flow<PagingData<MovieEntity>>

    suspend fun getUpcomingMovies(): Flow<PagingData<MovieEntity>>

    fun getTrendingDayMoviesPaged(): Flow<PagingData<MovieEntity>>

    suspend fun getTrendingWeekMoviesPaged(): Flow<PagingData<MovieEntity>>

    suspend fun getMovieDetail(movieId: Int): Flow<Resource<Movie>>

    suspend fun getPopularMovies(): Flow<Resource<List<Movie>>>

    suspend fun getTrendingDayMovies(): Flow<Resource<List<Movie>>>

    suspend fun getTrendingWeekMovies(): Flow<Resource<List<Movie>>>

    suspend fun getSimilarMovies(movieId: Int): Flow<Resource<List<Movie>>>

    suspend fun getCollection(collectionId: Int): Flow<Resource<List<Movie>>>

}

//suspend fun getPopularMovies() = movieApi.getPopularMovies(null, null)
//
//suspend fun getTopRatedMovies() = movieApi.getTopRatedMovies(null, null)
//
//suspend fun getMovieDetail(movieId: Int, append: String?) = movieApi.getMovieDetailInfo(movieId, append)