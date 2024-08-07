package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.TvSeries

data class SeriesDetailState(
    val isLoading: Boolean = false,
    val errorMsg: String ?= null,

    val series: TvSeries?= null,
    val credits: List<Credit> = emptyList(),
    val similar: List<Media> = emptyList()
)
