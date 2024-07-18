package hiendao.moviefinder.data.network.movie.model.credit.detail

data class ExternalIds(
    val facebook_id: String?,
    val freebase_id: String?,
    val freebase_mid: String?,
    val imdb_id: String?,
    val instagram_id: String?,
    val tiktok_id: Any?,
    val tvrage_id: Int?,
    val twitter_id: String?,
    val wikidata_id: String?,
    val youtube_id: Any?
)