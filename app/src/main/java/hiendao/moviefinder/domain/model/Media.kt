package hiendao.moviefinder.domain.model

data class Media(
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val genreIds: List<Int>,
    val voteAverage: Double,
    val voteCount: Int,
    val mediaType: String
)