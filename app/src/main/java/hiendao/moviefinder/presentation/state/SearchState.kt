package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.SearchModel

data class SearchState(
    val isLoading: Boolean = false,
    val errorMsg: String ?= null,

    val searchQuery: String = "",
    val searchPage: Int = 1,
    val searchResponse: List<Media> = emptyList()
)