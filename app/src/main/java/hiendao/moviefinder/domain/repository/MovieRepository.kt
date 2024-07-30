package hiendao.moviefinder.domain.repository

import androidx.paging.PagingData
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getMovieDetail(movieId: Int): Flow<Resource<Movie>>

    suspend fun getTopRatedMovies(page: Int = 1, isRefresh: Boolean = false, shouldCallNetwork: Boolean = false): Flow<Resource<List<Movie>>>

    suspend fun getPopularMovies(page: Int = 1, isRefresh: Boolean = false): Flow<Resource<List<Movie>>>

    suspend fun getTrendingDayMovies(page: Int = 1, isRefresh: Boolean = false): Flow<Resource<List<Movie>>>

    suspend fun getTrendingWeekMovies(page: Int = 1, isRefresh: Boolean = false, shouldCallNetwork: Boolean = false): Flow<Resource<List<Movie>>>

    suspend fun getSimilarMovies(movieId: Int): Flow<Resource<List<Movie>>>

    suspend fun getCollection(collectionId: Int): Flow<Resource<List<Movie>>>

    suspend fun getMoviesWithCreditId(creditId: Int): Flow<Resource<List<Movie>>>

    suspend fun discoverMovies(
        releaseDateLte: String,
        page: Int,
        withGenres: String?,
        sortBy: String = "popularity.desc",
        voteCountGte: Float?
    ): Flow<Resource<List<Movie>>>
}