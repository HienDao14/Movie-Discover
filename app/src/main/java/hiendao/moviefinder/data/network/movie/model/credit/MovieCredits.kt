package hiendao.moviefinder.data.network.movie.model.credit

import hiendao.moviefinder.data.network.movie.model.list.MovieDTO

data class MovieCredits(
    val cast: List<MovieDTO>?,
    val crew: List<MovieDTO>?,
    val id: Int?
)