package hiendao.moviefinder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import hiendao.moviefinder.util.Category

@Entity(tableName = "tv_series")
data class TvSeriesEntity(
    val adult: Boolean,
    val backdropPath: String,
    val createdBy: String,
    val credits: String,
    val episodeRuntime: String,
    val firstAirDate: String,
    val genres: String,
    val homepage: String,
    @PrimaryKey
    val id: Int,
    val images: String,
    val inProduction: Boolean,
    val languages: String,
    val lastAirDate: String,
    val lastEpisodeToAir: String,
    val name: String,
    val networks: String,
    val nextEpisodeToAir: String,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val originCountry: String,
    val originalLanguage: String,
    val originalName: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val productionCompanies: String,
    val productionCountries: String,
    val seasons: String,
    val spokenLanguages: String,
    val status: String,
    val tagline: String,
    val type: String,
    val videos: String,
    val similar: String,
    val voteAverage: Double,
    val voteCount: Int,
    val category: String = Category.MOVIE.name,
    val addedToFavorite: Int = 0,
    val addedInFavoriteDate: String = ""
)
