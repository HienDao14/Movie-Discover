package hiendao.moviefinder.data.network.movie.dataproviders

import hiendao.moviefinder.data.network.movie.MovieApi
import javax.inject.Inject

class MovieDataProvider @Inject constructor(
    private val movieApi: MovieApi
) {
    suspend fun getPopularMovies() = movieApi.getPopularMovies(1)

    suspend fun getTopRatedMovies() = movieApi.getTopRatedMovies(1)

    suspend fun getMovieDetail(movieId: Int, append: String?) = movieApi.getMovieDetailInfo(movieId, append)
}