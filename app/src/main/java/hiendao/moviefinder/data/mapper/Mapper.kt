package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.network.movie.model.collection.PartCollection
import hiendao.moviefinder.data.network.movie.model.detail.MovieDetailDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieListResponse
import hiendao.moviefinder.data.network.movie.model.list.MovieListWithDateResponse
import hiendao.moviefinder.data.network.util.base_url.BaseUrl.BASE_IMAGE_URL
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.util.Category

fun makeFullUrl(path: String): String {
    return "${BASE_IMAGE_URL}${path}"
}


fun MovieListWithDateResponse.toMovieListResponse(): MovieListResponse {
    return MovieListResponse(
        page, results, total_pages, total_results
    )
}

fun List<MovieDTO>.toListMovieEntity(category: Category, page: Int): List<MovieEntity> {
    return this.mapIndexed { index, movieDTO ->
        movieDTO.toMovieEntity(category, (page - 1) * 20 + index + 1)
    }
}

fun List<MovieEntity>.toListMovie(): List<Movie> {
    return this.map {
        it.toMovie()
    }
}

fun MovieDTO.toMovieEntity(category: Category, index: Int): MovieEntity {
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
        category = category.name,
        categoryIndex = index,
        videos = "",
        collectionId = 0
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
        images = images.split(",").map { it },
        videos = videos?.split(",")?.map { it } ?: emptyList(),
        collectionId = collectionId
    )
}

fun MovieDetailDTO.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        adult = adult,
        backdropPath = backdrop_path ?: "",
        genreIds = genres.joinToString(","),
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
        images = images?.posters?.joinToString(",") ?: "",
        category = Category.MOVIE.name,
        categoryIndex = -1,
        videos = videos.results.joinToString(",") { it.key },
        collectionId = belongs_to_collection?.id ?: -1
    )
}

fun MovieDetailDTO.toMovie(): Movie{
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
        productionCompany = production_companies.map{ it.name },
        originCountry = origin_country,
        similar = similar?.results?.map { it.id } ?: emptyList(),
        images = images?.posters?.map { it.file_path } ?: emptyList(),
        videos = videos.results.map { it.key },
        collectionId = belongs_to_collection?.id ?: -1
    )
}

fun MovieDTO.toMovie(): Movie{
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
        collectionId = 0
    )
}

fun PartCollection.toMovieEntity(): MovieEntity{
    return MovieEntity(
        id = id,
        adult = adult,
        backdropPath = backdrop_path,
        genreIds = genre_ids.joinToString(","),
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
        collectionId = 0
    )
}

fun PartCollection.toMovie(): Movie{
    return Movie(
        id = id,
        adult = adult,
        backdropPath = backdrop_path,
        genreIds = genre_ids,
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
        collectionId = 0
    )
}