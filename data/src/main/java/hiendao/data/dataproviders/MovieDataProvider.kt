package hiendao.data.dataproviders

import hiendao.data.ApiService
import javax.inject.Inject

class MovieDataProvider @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getMovieList() = apiService.getPopularMovies(null, null)
}