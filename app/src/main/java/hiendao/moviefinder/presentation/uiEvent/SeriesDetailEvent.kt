package hiendao.moviefinder.presentation.uiEvent

sealed class SeriesDetailEvent {

    data class Refresh(val type: String, val seriesId : Int) : SeriesDetailEvent()

    data class AddToFavorite(val favorite: Int, val date: String, val seriesId: Int): SeriesDetailEvent()
}