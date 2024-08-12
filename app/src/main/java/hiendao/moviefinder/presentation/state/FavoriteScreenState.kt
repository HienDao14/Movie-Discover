package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.model.TvSeries

data class FavoriteScreenState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,

    val movieFavorite: List<Media> = emptyList(),
    val creditFavorite: List<Credit> = emptyList(),
    val seriesFavorite: List<Media> = emptyList(),

    val moviePage: Int = 1,
    val creditPage: Int = 1,
    val seriesPage: Int = 1
)
