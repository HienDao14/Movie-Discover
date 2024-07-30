package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.network.search.SearchDTO
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.model.SearchModel
import hiendao.moviefinder.util.Category

fun SearchDTO.toMovieEntity(): MovieEntity{
    return MovieEntity(
        id = id,
        adult = adult ?: false,
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids?.joinToString(",") ?: "",
        originalLanguage = original_language ?: "",
        originalTitle = original_title ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = poster_path ?: "",
        releaseDate = release_date ?: "",
        title = title ?: "",
        video = video ?: false,
        voteAverage = vote_average ?: 0.0,
        voteCount = vote_count ?: 0,
        budget = 0,
        homepage = "",
        revenue = 0,
        runtime = 0,
        status = "",
        tagline = "",
        productionCompany = "",
        originCountry = "",
        similar = "",
        images = "",
        category = Category.MOVIE.name,
        categoryIndex = -1,
        videos = "",
        collectionId = -1,
        credits = ""
    )
}

fun SearchDTO.toMovie(): Movie{
    return Movie(
        id = id,
        adult = adult ?: false,
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids ?: emptyList(),
        originalLanguage = original_language ?: "",
        originalTitle = original_title ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = poster_path ?: "",
        releaseDate = release_date ?: "",
        title = title ?: "",
        video = video ?: false,
        voteAverage = vote_average ?: 0.0,
        voteCount = vote_count ?: 0,
        budget = 0,
        homepage = "",
        revenue = 0,
        runtime = 0,
        status = "",
        tagline = "",
        productionCompany = emptyList(),
        originCountry = emptyList(),
        similar = emptyList(),
        images = emptyList(),
        videos = emptyList(),
        collectionId = -1
    )
}

fun SearchDTO.toSearchModel() : SearchModel{
    return SearchModel(
        id = id,
        title = if(media_type == "movie") title ?: "" else name ?: "",
        posterPath = poster_path ?: "",
        voteAverage = vote_average ?: 0.0,
        voteCount =  vote_count ?: 0,
        genres = genre_ids ?: emptyList()
    )
}