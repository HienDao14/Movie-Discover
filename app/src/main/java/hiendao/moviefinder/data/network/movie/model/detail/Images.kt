package hiendao.moviefinder.data.network.movie.model.detail

data class Images(
    val backdrops: List<ImageResponse>,
    val logos: List<ImageResponse>,
    val posters: List<ImageResponse>
)