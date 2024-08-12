package hiendao.moviefinder.data.network.search

import hiendao.moviefinder.data.network.search.model.SearchResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/multi")
    suspend fun getSearchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): SearchResponseDTO
}