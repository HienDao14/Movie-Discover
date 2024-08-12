package hiendao.moviefinder.domain.model

data class SearchModel(
    val id: Int,
    val title: String,
    val posterPath: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<Int>
)
