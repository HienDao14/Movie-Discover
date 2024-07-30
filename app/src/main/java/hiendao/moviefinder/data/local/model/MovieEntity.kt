package hiendao.moviefinder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import hiendao.moviefinder.util.Category

@Entity(tableName = "movie")
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val backdropPath: String,
    val genreIds: String,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Int,
    val homepage: String,
    val revenue: Long,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val productionCompany: String,
    val originCountry: String,
    val similar : String,
    val images: String,
    val category: String,
    val categoryIndex : Int,
    val videos: String?,
    val collectionId: Int,
    val mediaType : String?= null,
    val credits: String,
    val addedToFavorite: Int = 0
)