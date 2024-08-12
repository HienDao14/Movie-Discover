package hiendao.moviefinder.data.network.movie


import hiendao.moviefinder.data.network.movie.model.collection.Collection
import hiendao.moviefinder.data.network.movie.model.credit.Credits
import hiendao.moviefinder.data.network.movie.model.credit.MovieCredits
import hiendao.moviefinder.data.network.movie.model.credit.detail.CreditDetail
import hiendao.moviefinder.data.network.movie.model.detail.MovieDetailDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieListResponse
import hiendao.moviefinder.data.network.movie.model.list.MovieListWithDateResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    //baseUrl: https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc

    //Movie API
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int?
    ): MovieListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int?
    ): MovieListWithDateResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int?
    ): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int?
    ): MovieListWithDateResponse

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovie(
        @Path("time_window") timeWindow: String,
        @Query("page") page: Int?
    ): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetailInfo(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") append: String? = "images,similar,videos"
    ): MovieDetailDTO

    @GET("collection/{collection_id}")
    suspend fun getMovieCollection(
        @Path("collection_id") collectionId: Int
    ): Collection

    @GET("movie/{movie_id}/credits")
    suspend fun getCredits(
        @Path("movie_id") movieId: Int
    ): Credits

    @GET("person/{person_id}")
    suspend fun getCreditDetail(
        @Path("person_id") personId: Int,
        @Query("append_to_response") append: String? = "movie_credits,external_ids"
    ): CreditDetail

    @GET("person/{person_id}/movie_credits")
    suspend fun getMovieCredits(
        @Path("person_id") personId: Int
    ): MovieCredits

    @GET("discover/movie")
    suspend fun getMovieDiscover(
        @Query("release_date.lte") releaseDateLte: String,
        @Query("page") page: Int = 1,
        @Query("with_genres") withGenres: String?,
        @Query("sort_by") sortBy: String,
        @Query("vote_count.gte") voteCountGte: Float?
    ): MovieListResponse
}