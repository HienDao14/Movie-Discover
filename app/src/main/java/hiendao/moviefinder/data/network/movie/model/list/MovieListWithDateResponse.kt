package hiendao.moviefinder.data.network.movie.model.list

import hiendao.moviefinder.data.network.movie.model.detail.Dates

data class MovieListWithDateResponse(
    val dates: Dates,
    val page: Int,
    val results: List<MovieDTO>,
    val total_pages: Int,
    val total_results: Int
)