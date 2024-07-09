package hiendao.moviefinder.data.network.tvseries

import hiendao.moviefinder.data.network.tvseries.model.detail.TvSeriesDetail
import hiendao.moviefinder.data.network.tvseries.model.list.TvSeriesListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvSeriesApi {
    //TV Series API
    @GET("tv/popular")
    suspend fun getPopularTvSeries(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): TvSeriesListResponse

    @GET("tv/airing_today")
    suspend fun getAiringTodayTvSeries(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): TvSeriesListResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedTvSeries(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): TvSeriesListResponse

    @GET("tv/{seriesId}")
    suspend fun getTvSeriesDetail(
        @Path("seriesId") seriesId: Int,
        @Query("append_to_response") append: String?
    ): TvSeriesDetail
}