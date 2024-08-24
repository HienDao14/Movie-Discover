package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.local.model.TvSeriesEntity
import hiendao.moviefinder.data.network.search.model.SearchDTO
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.model.SearchModel
import hiendao.moviefinder.util.Category

fun SearchDTO.toMovieEntity(category: String, index: Int, favorite: Int = 0, favoriteDate: String = "",  categoryAddedDate : String = ""): MovieEntity{
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
        category = category,
        categoryIndex = index,
        videos = "",
        collectionId = -1,
        credits = "",
        addedToFavorite = favorite,
        addedInFavoriteDate = favoriteDate,
        categoryDateAdded = categoryAddedDate
    )
}

fun SearchDTO.toTvSeriesEntity(): TvSeriesEntity{
    return TvSeriesEntity(
        adult = adult ?: false,
        backdropPath = backdrop_path ?: "",
        createdBy = "",
        credits = "",
        episodeRuntime = "",
        firstAirDate = first_air_date ?: "",
        genres = genre_ids?.joinToString(",") { it.toString() } ?: "",
        homepage = "",
        id = id,
        images = "",
        inProduction = false,
        languages = "",
        lastAirDate = "",
        lastEpisodeToAir = "",
        name = name ?: "",
        networks = "",
        nextEpisodeToAir = "",
        numberOfEpisodes = 0,
        numberOfSeasons = 0,
        originCountry = origin_country?.joinToString(",") { it ?: "" } ?: "",
        originalLanguage = original_language ?: "",
        originalName = original_name ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = poster_path ?: "",
        productionCompanies = "",
        productionCountries = "",
        seasons = "",
        spokenLanguages = "",
        status = "",
        tagline = "",
        type = "",
        videos = "",
        similar = "",
        voteAverage = vote_average ?: 0.0,
        voteCount = vote_count ?: 0,
        addedToFavorite = 0,
        addedInFavoriteDate = "",
        category = ""
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

fun SearchDTO.toMedia(): Media{
    return Media(
        id = id,
        title = if(media_type == "movie") title ?: "" else name ?: "",
        posterPath = poster_path ?: "",
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids ?: emptyList(),
        voteAverage = vote_average ?: 0.0,
        voteCount =  vote_count ?: 0,
        mediaType = media_type ?: "movie"
    )
}