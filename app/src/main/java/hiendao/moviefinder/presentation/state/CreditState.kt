package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Movie

data class CreditState(
    val isLoading: Boolean = false,
    val errorMsg: String ?= null,

    val changeFavorite: Boolean ?= null,
    val loadingFor: String ?= "",
    val movies: List<Movie> = emptyList(),
    val creditId: Int = 0,
    val creditDetail : Credit ?= null
)
