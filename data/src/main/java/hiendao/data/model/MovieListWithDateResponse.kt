package hiendao.data.model

data class MovieListWithDateResponse(
    val dates: Dates,
    val page: Int,
    val results: List<MovieDTO>,
    val total_pages: Int,
    val total_results: Int
)