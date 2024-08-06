package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie

data class MovieDetailState(
    val isLoading: Boolean = false,
    val isRefresh: Boolean = false,

    val errorMsg: String = "",

    val movieId: Int? = null,
    val collectionId: Int? = null,

    val movie: Movie? = null,
    val listSimilar: List<Int> = emptyList(),
    val similarVideos: List<Media> = emptyList(),
    val collectionVideos: List<Media> = emptyList(),
    val listCredit: List<Credit> = emptyList()
)
