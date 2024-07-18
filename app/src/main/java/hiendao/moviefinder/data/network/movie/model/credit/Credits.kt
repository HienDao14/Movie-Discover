package hiendao.moviefinder.data.network.movie.model.credit

data class Credits(
    val cast: List<Cast>?,
    val crew: List<Crew>?,
    val id: Int?
)