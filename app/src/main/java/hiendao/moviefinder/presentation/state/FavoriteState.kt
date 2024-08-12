package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Movie

data class FavoriteState(
    val isLoading : Boolean = false,
    val isSuccess: Boolean? = null,
    val errorMsg: String? = null
)