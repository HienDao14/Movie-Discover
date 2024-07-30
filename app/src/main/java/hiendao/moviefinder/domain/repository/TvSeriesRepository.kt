package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface TvSeriesRepository {

    suspend fun getTrendingDayTvSeries(): Flow<Resource<List<TvSeries>>>
}