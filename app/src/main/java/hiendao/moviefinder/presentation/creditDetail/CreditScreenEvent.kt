package hiendao.moviefinder.presentation.creditDetail

sealed class CreditScreenEvent {
    data class ChangeFavorite(val favorite: Int): CreditScreenEvent()
    data class Refresh(val type: String): CreditScreenEvent()

}