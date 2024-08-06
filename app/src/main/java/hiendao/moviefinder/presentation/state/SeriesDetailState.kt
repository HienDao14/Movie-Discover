package hiendao.moviefinder.presentation.state

import hiendao.moviefinder.domain.model.TvSeries

data class SeriesDetailState(
    val isLoading: Boolean = false,
    val errorMsg: String ?= null,

    val series: TvSeries?= null
)
