package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun getMovieFavorite(page: Int = 1): Flow<Resource<List<Media>>>

    suspend fun getCreditFavorite(page: Int = 1): Flow<Resource<List<Credit>>>

    suspend fun getTvSeriesFavorite(page: Int = 1): Flow<Resource<List<Media>>>
}