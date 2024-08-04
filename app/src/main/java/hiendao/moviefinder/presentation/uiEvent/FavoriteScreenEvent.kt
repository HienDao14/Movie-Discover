package hiendao.moviefinder.presentation.uiEvent

sealed class FavoriteScreenEvent {

    data class Refresh(val type: String) : FavoriteScreenEvent()

    data class OnPaginate(val type: String): FavoriteScreenEvent()

}