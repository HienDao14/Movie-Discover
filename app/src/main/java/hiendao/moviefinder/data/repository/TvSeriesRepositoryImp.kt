package hiendao.moviefinder.data.repository

import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TvSeriesRepositoryImp @Inject constructor(

): TvSeriesRepository {

    override suspend fun getTrendingDayTvSeries(): Flow<Resource<List<TvSeries>>> {
        TODO("Not yet implemented")
    }
}