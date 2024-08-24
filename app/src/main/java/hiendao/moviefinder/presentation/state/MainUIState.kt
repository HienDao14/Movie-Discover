package hiendao.moviefinder.presentation.state

import androidx.paging.PagingData
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import kotlinx.coroutines.flow.Flow

data class MainUIState(
    val isLoading: Boolean = false,
    val isRefresh: Boolean = false,
    val errorMsg: String? = null,
    val topRatedMovies: List<Movie> = emptyList(),
    val trendingDayMovies: List<Movie> = emptyList(),
    val trendingWeekMovies: List<Movie> = emptyList(),

    //Discover
    val discoverMedia: List<Media> = emptyList(),
    val withGenres: String? = null,
    val sortBy: String = "popularity.desc",
    val voteCountGte: Float? = 0f,

    val moviePage: Int = 1,
    val pagingType: String = "",
    val popularTvSeries: List<Media> = emptyList(),
    val topRatedTvSeries: List<Media> = emptyList(),
)
