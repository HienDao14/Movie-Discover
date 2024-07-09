package hiendao.moviefinder.data.network.movie.model.list

data class MovieListResponse(
    val page: Int,
    val results: List<MovieDTO>,
    val total_pages: Int,
    val total_results: Int
)