package hiendao.moviefinder.data.network.movie.model.detail

import hiendao.moviefinder.data.network.movie.model.credit.Credits
import hiendao.moviefinder.data.network.movie.model.video.Videos
import hiendao.moviefinder.domain.model.Genres

data class MovieDetailDTO(
    val adult: Boolean,
    val backdrop_path: String?,
    val belongs_to_collection: BelongsToCollection?,
    val budget: Int,
    val genres: List<Genres>,
    val homepage: String,
    val id: Int,
    val images: Images?,
    val imdb_id: String,
    val origin_country: List<String>,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val production_companies: List<ProductionCompany>,
    val production_countries: List<ProductionCountry>,
    val release_date: String,
    val revenue: Long,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguage>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int,
    val similar: Similar?,
    val videos: Videos
)