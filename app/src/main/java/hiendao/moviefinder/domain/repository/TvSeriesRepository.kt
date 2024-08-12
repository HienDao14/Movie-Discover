package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface TvSeriesRepository {

    suspend fun getPopularTvSeries(
        page: Int = 1,
        isRefresh: Boolean = false,
        shouldCallNetwork: Boolean = false
    ): Flow<Resource<List<Media>>>

    suspend fun getTrendingDayTvSeries(): Flow<Resource<List<TvSeries>>>

    suspend fun getTopRatedTvSeries(
        page: Int = 1,
        isRefresh: Boolean = false,
        shouldCallNetwork: Boolean = false
    ): Flow<Resource<List<Media>>>

    suspend fun discoverTvSeries(
        firstAirDateLte: String,
        page: Int,
        withGenres: String?,
        sortBy: String,
        voteCountGte: Float?
    ): Flow<Resource<List<Media>>>

    suspend fun getTvSeriesDetail(
        isRefresh: Boolean,
        id: Int
    ): Flow<Resource<TvSeries>>

    suspend fun changeFavorite(
        favorite: Int, addedDate: String, seriesId: Int
    ): Flow<Resource<Boolean>>
}