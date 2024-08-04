package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Movie

data class FavoriteScreenState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,

    val movieFavorite: List<Movie> = emptyList(),
    val creditFavorite: List<Credit> = emptyList(),

    val moviePage: Int = 1,
    val creditPage: Int = 1
)
