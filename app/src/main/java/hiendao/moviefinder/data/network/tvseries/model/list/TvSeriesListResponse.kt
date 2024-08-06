package hiendao.moviefinder.data.network.tvseries.model.list

data class TvSeriesListResponse(
    val page: Int,
    val results: List<TvSeriesDTO>,
    val total_pages: Int,
    val total_results: Int
)