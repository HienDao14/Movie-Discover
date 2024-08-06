package hiendao.moviefinder.presentation.uiEvent

sealed class MainEvent {

    data class Refresh(val type: String): MainEvent()

    data class OnPaginate(val type: String): MainEvent()

    data class StartLoad(val type: String) : MainEvent()

    data class StartDiscover(val type: String, val sortBy: String, val voteCount: Float?, val withGenres: String?): MainEvent()

}