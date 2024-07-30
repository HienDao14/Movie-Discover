package hiendao.moviefinder.presentation.uiEvent

sealed class SearchEvent {
    data class Refresh(val query: String) : SearchEvent()

    data class OnPaginate(val type: String) : SearchEvent()

    data class OnQueryChange(val query: String): SearchEvent()

    data class OnExit(val type: String): SearchEvent()
}