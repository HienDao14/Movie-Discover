package hiendao.data

import hiendao.data.model.MovieDetail
import hiendao.data.model.MovieListResponse
import hiendao.data.model.MovieListWithDateResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //baseUrl: https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc

    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MovieListResponse

    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MovieListWithDateResponse

    @GET("3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MovieListResponse

    @GET("3/movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("language") language: String?,
        @Query("page") page: Int?
    ): MovieListWithDateResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetailInfo(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") append: String
    ): MovieDetail

}