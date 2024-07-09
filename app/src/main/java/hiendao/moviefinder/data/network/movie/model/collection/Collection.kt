package hiendao.moviefinder.data.network.movie.model.collection

data class Collection(
    val backdrop_path: String,
    val id: Int,
    val name: String,
    val overview: String,
    val parts: List<PartCollection>,
    val poster_path: String
)