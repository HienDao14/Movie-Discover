package hiendao.moviefinder.data.network.tvseries

import hiendao.moviefinder.data.network.tvseries.model.detail.TvSeriesDetailDTO
import hiendao.moviefinder.data.network.tvseries.model.list.TvSeriesListResponse
import hiendao.moviefinder.data.network.tvseries.model.season.SeasonDTO
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

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTvSeries(
        @Path("time_window") timeWindow : String ?= "day"
    ): TvSeriesListResponse

    @GET("tv/{series_id}")
    suspend fun getTvSeriesDetail(
        @Path("series_id") seriesId: Int,
        @Query("append_to_response") append: String? = "credits"
    ): TvSeriesDetailDTO

    @GET("tv/{series_id}/season/{season_number}")
    suspend fun getTvSeriesSeasonDetail(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int
    ): SeasonDTO
}