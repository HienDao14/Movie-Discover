package hiendao.moviefinder.domain.model.movie

data class MovieListItem(
    val genreIds: List<Int>,
    val posterPath: String,
    val title: String,
    val voteAverage: Double,
    val overview: String,
    val releaseDate: String
)
