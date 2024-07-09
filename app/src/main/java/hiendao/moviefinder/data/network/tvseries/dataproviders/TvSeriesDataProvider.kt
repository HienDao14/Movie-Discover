package hiendao.moviefinder.data.network.tvseries.dataproviders

import hiendao.moviefinder.data.network.tvseries.TvSeriesApi
import javax.inject.Inject

class TvSeriesDataProvider @Inject constructor(
    private val tvSeriesApi: TvSeriesApi
) {

    suspend fun getPopularTvSeries() = tvSeriesApi.getPopularTvSeries(null, null)

    suspend fun getAiringTodayTvSeries() = tvSeriesApi.getAiringTodayTvSeries(null, null)

    suspend fun getTopRatedTvSeries() = tvSeriesApi.getTopRatedTvSeries(null, null)

    suspend fun getTvSeriesDetail(tvSeriesId: Int, append: String?) = tvSeriesApi.getTvSeriesDetail(tvSeriesId, append)
}