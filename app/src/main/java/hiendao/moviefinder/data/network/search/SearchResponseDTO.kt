package hiendao.moviefinder.data.network.search

data class SearchResponseDTO(
    val page: Int?,
    val results: List<SearchDTO>?,
    val total_pages: Int?,
    val total_results: Int?
)