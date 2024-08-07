package hiendao.moviefinder.data.network.tvseries.model.detail

import hiendao.moviefinder.data.network.tvseries.model.list.TvSeriesDTO

data class Similar(
    val page: Int?,
    val results: List<TvSeriesDTO>?,
    val total_pages: Int?,
    val total_results: Int?
)