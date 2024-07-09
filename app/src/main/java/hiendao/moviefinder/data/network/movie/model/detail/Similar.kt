package hiendao.moviefinder.data.network.movie.model.detail

import hiendao.moviefinder.data.network.movie.model.list.MovieDTO

data class Similar(
    val page: Int,
    val results: List<MovieDTO>,
    val total_pages: Int,
    val total_results: Int
)
