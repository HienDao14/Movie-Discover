package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.movie.Movie

data class MainUIState(
    val isLoading: Boolean = false,
    val isRefresh: Boolean = false,
    val popularMovies: List<Movie> = emptyList(),
    val trendingDayMovies: List<Movie> = emptyList(),
    val trendingWeekMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList()
)
