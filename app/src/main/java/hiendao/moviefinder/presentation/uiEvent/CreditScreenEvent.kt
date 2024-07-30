package hiendao.moviefinder.presentation.uiEvent

sealed class CreditScreenEvent {
    data class ChangeFavorite(val favorite: Int, val creditId: Int): CreditScreenEvent()
    data class Refresh(val type: String): CreditScreenEvent()

}