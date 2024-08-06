package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface TvSeriesRepository {

    suspend fun getPopularTvSeries(page: Int = 1): Flow<Resource<List<TvSeries>>>

    suspend fun getTrendingDayTvSeries(): Flow<Resource<List<TvSeries>>>

    suspend fun discoverTvSeries(
        firstAirDateLte: String,
        page: Int,
        withGenres: String?,
        sortBy: String,
        voteCountGte: Float?
    ): Flow<Resource<List<Media>>>
}