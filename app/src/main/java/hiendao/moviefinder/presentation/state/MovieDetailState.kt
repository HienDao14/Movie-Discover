package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.movie.Movie

data class MovieDetailState(
    val isLoading: Boolean = false,
    val isRefresh: Boolean = false,

    val errorMsg: String = "",

    val collectionId: Int? = null,

    val movie: Movie? = null,
    val listSimilar: List<Int> = emptyList(),
    val similarVideos: List<Movie> = emptyList(),
    val collectionVideos: List<Movie> = emptyList()
)
