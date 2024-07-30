package hiendao.moviefinder.domain.model

import hiendao.moviefinder.util.Category


data class Movie(
    val id: Int,
    val adult: Boolean,
    val backdropPath: String,
    val genreIds: List<Int>,
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
    val productionCompany: List<String>,
    val originCountry: List<String>,
    val similar : List<Int>,
    val images: List<String>,
    val videos: List<String>,
    val collectionId: Int,
    val mediaType: String ?= null
)
