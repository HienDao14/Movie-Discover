package hiendao.moviefinder.data.network.tvseries

import hiendao.moviefinder.data.network.movie.model.credit.Credits
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
        @Query("page") page: Int?
    ): TvSeriesListResponse

    @GET("tv/airing_today")
    suspend fun getAiringTodayTvSeries(
        @Query("page") page: Int?
    ): TvSeriesListResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedTvSeries(
        @Query("page") page: Int?
    ): TvSeriesListResponse

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingDayTvSeries(
        @Path("time_window") timeWindow : String ?= "day"
    ): TvSeriesListResponse

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingWeekTvSeries(
        @Path("time_window") timeWindow : String ?= "week"
    ): TvSeriesListResponse

    @GET("tv/{series_id}")
    suspend fun getTvSeriesDetail(
        @Path("series_id") seriesId: Int,
        @Query("append_to_response") append: String? = "credits,images,videos,similar"
    ): TvSeriesDetailDTO

    @GET("tv/{series_id}/season/{season_number}")
    suspend fun getTvSeriesSeasonDetail(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int
    ): SeasonDTO

    @GET("discover/tv")
    suspend fun getTvSeriesDiscover(
        @Query("first_air_date.lte") firstAirDateLte: String,
        @Query("page") page: Int = 1,
        @Query("with_genres") withGenres: String?,
        @Query("sort_by") sortBy: String,
        @Query("vote_count.gte") voteCountGte: Float?
    ): TvSeriesListResponse

    @GET("tv/{series_id}/credits")
    suspend fun getCredits(
        @Path("series_id") seriesId: Int
    ): Credits
}