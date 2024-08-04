package hiendao.moviefinder.presentation.uiEvent

sealed class MovieDetailEvent{
    data class Refresh(val type: String, val movieId : Int) : MovieDetailEvent()

    data class onPaginate(val type: String): MovieDetailEvent()

    data class AddToFavorite(val favorite: Int, val date: String, val movieId: Int): MovieDetailEvent()
}
