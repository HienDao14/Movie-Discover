package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.network.movie.model.collection.PartCollection
import hiendao.moviefinder.data.network.movie.model.credit.detail.MovieCast
import hiendao.moviefinder.data.network.movie.model.credit.detail.MovieCrew
import hiendao.moviefinder.data.network.movie.model.detail.MovieDetailDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieListResponse
import hiendao.moviefinder.data.network.movie.model.list.MovieListWithDateResponse
import hiendao.moviefinder.data.network.util.base_url.BaseUrl.BASE_IMAGE_URL
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.util.Category

fun makeFullUrl(path: String): String {
    return "${BASE_IMAGE_URL}${path}"
}

fun MovieDTO.toMovieEntity(category: String, index: Int, favorite: Int = 0, favoriteDate: String = ""): MovieEntity {
    return MovieEntity(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids.joinToString(","),
        originalLanguage = original_language,
        originalTitle = original_title,
        overview = overview,
        popularity = popularity,
        posterPath = poster_path ?: "",
        releaseDate = release_date,
        title = title,
        video = video,
        voteAverage = vote_average,
        voteCount = vote_count,
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
        addedInFavoriteDate = favoriteDate
    )
}

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdropPath,
        genreIds =
        try {
            genreIds.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        },
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount,
        budget = budget,
        homepage = homepage,
        revenue = revenue,
        runtime = runtime,
        status = status,
        tagline = tagline,
        productionCompany = productionCompany.split(",").map { it },
        originCountry = originCountry.split(",").map { it },
        similar = try {
            similar.split(",").map { it.toInt() }
        } catch (e: Exception) {
            listOf(-1, -2)
        },
        images = images.split(";").map { it },
        videos = videos?.split(",")?.map { it } ?: emptyList(),
        collectionId = collectionId,
        addedInFavorite = addedToFavorite == 1
    )
}

fun MovieEntity.toMedia(): Media{
    return Media(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genreIds = genreIds.split(",").map { it.toInt() },
        voteAverage = voteAverage,
        voteCount = voteCount,
        mediaType = "Movie"
    )
}

fun MovieDetailDTO.toMovieEntity(category: String, index: Int, favorite: Int = 0, favoriteDate: String = ""): MovieEntity {
    return MovieEntity(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genres.joinToString(",") { it.id.toString() },
        originalLanguage = original_language,
        originalTitle = original_title,
        overview = overview,
        popularity = popularity,
        posterPath = poster_path,
        releaseDate = release_date,
        title = title,
        video = video,
        voteAverage = vote_average,
        voteCount = vote_count,
        budget = budget,
        homepage = homepage,
        revenue = revenue,
        runtime = runtime,
        status = status,
        tagline = tagline,
        productionCompany = production_companies.joinToString(",") { it.name },
        originCountry = origin_country.joinToString(","),
        similar = similar?.results?.joinToString(",") { it.id.toString() } ?: "",
        images = (images?.posters?.joinToString(",") { it.file_path } ?: "") + ";"
                + (images?.backdrops?.joinToString(",") { it.file_path } ?: ""),
        category = category,
        categoryIndex = index,
        videos = videos.results.joinToString(",") { it.key },
        collectionId = belongs_to_collection?.id ?: -1,
        credits = "",
        addedToFavorite = favorite,
        addedInFavoriteDate = favoriteDate
    )
}

fun MovieDetailDTO.toMovie(): Movie {
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genres.map { it.id },
        originalLanguage = original_language,
        originalTitle = original_title,
        overview = overview,
        popularity = popularity,
        posterPath = poster_path,
        releaseDate = release_date,
        title = title,
        video = video,
        voteAverage = vote_average,
        voteCount = vote_count,
        budget = budget,
        homepage = homepage,
        revenue = revenue,
        runtime = runtime,
        status = status,
        tagline = tagline,
        productionCompany = production_companies.map { it.name },
        originCountry = origin_country,
        similar = similar?.results?.map { it.id } ?: emptyList(),
        images = listOf(images?.posters?.joinToString(",") { it.file_path } ?: "", images?.backdrops?.joinToString(",") { it.file_path } ?: "") ,
        videos = videos.results.map { it.key },
        collectionId = belongs_to_collection?.id ?: -1
    )
}

fun MovieDTO.toMovie(): Movie {
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids,
        originalLanguage = original_language,
        originalTitle = original_title,
        overview = overview,
        popularity = popularity,
        posterPath = poster_path ?: "",
        releaseDate = release_date,
        title = title,
        video = video,
        voteAverage = vote_average,
        voteCount = vote_count,
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

fun PartCollection.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids.joinToString(","),
        originalLanguage = original_language,
        originalTitle = original_title,
        overview = overview,
        popularity = popularity,
        posterPath = poster_path ?: "",
        releaseDate = release_date,
        title = title,
        video = video,
        voteAverage = vote_average,
        voteCount = vote_count,
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
        categoryIndex = 0,
        videos = "",
        collectionId = -1,
        mediaType = media_type,
        credits = ""
    )
}

fun PartCollection.toMovie(): Movie {
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids,
        originalLanguage = original_language,
        originalTitle = original_title,
        overview = overview,
        popularity = popularity,
        posterPath = poster_path ?: "",
        releaseDate = release_date,
        title = title,
        video = video,
        voteAverage = vote_average,
        voteCount = vote_count,
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
        collectionId = -1,
        mediaType = media_type
    )
}


fun MovieCast.toMovieEntity(category: Category, index: Int): MovieEntity {
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
        category = category.name,
        categoryIndex = index,
        videos = "",
        collectionId = -1,
        credits = ""
    )
}

fun MovieCrew.toMovieEntity(category: Category, index: Int): MovieEntity {
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
        category = category.name,
        categoryIndex = index,
        videos = "",
        collectionId = -1,
        credits = ""
    )
}


fun MovieDTO.toMedia(): Media{
    return Media(
        id = id,
        title = title,
        posterPath = poster_path ?: "",
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids,
        voteAverage = vote_average,
        voteCount = vote_count,
        mediaType = "Movie"
    )
}

fun Movie.toMedia(): Media{
    return Media(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genreIds = genreIds,
        voteAverage = voteAverage,
        voteCount = voteCount,
        mediaType = "Movie"
    )
}

fun List<MovieDTO>.toListMovieEntity(category: String, page: Int): List<MovieEntity> {
    return this.mapIndexed { index, movieDTO ->
        movieDTO.toMovieEntity(category, (page - 1) * 20 + index + 1)
    }
}

fun List<MovieEntity>.toListMovie(): List<Movie> {
    return this.map {
        it.toMovie()
    }
}

fun List<MovieEntity>.entityToListMedia(): List<Media>{
    return this.map {
        it.toMedia()
    }
}

fun List<Movie>.toListMedia(): List<Media>{
    return this.map {
        it.toMedia()
    }
}