package hiendao.moviefinder.data.network.movie


import hiendao.moviefinder.data.network.movie.model.collection.Collection
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
}