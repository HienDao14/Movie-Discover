package hiendao.moviefinder.data.network.tvseries.model.season

import hiendao.moviefinder.data.network.tvseries.model.detail.Cast
import hiendao.moviefinder.data.network.tvseries.model.detail.Crew

data class Credits(
    val cast: List<Cast?>?,
    val crew: List<Crew?>?
)