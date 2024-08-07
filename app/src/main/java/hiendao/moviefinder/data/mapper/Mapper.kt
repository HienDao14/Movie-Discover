package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.CreditEntity
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.local.model.TvSeriesEntity
import hiendao.moviefinder.data.network.movie.model.collection.PartCollection
import hiendao.moviefinder.data.network.movie.model.credit.Cast
import hiendao.moviefinder.data.network.movie.model.credit.Crew
import hiendao.moviefinder.data.network.movie.model.credit.detail.CreditDetail
import hiendao.moviefinder.data.network.movie.model.credit.detail.MovieCast
import hiendao.moviefinder.data.network.movie.model.credit.detail.MovieCrew
import hiendao.moviefinder.data.network.movie.model.detail.MovieDetailDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieDTO
import hiendao.moviefinder.data.network.movie.model.list.MovieListResponse
import hiendao.moviefinder.data.network.movie.model.list.MovieListWithDateResponse
import hiendao.moviefinder.data.network.util.base_url.BaseUrl.BASE_IMAGE_URL
import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.util.Category

fun makeFullUrl(path: String): String {
    return "${BASE_IMAGE_URL}${path}"
}


fun MovieListWithDateResponse.toMovieListResponse(): MovieListResponse {
    return MovieListResponse(
        page, results, total_pages, total_results
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

fun PartCollection.toTvSeriesEntity(): TvSeriesEntity{
    return TvSeriesEntity(
        adult = adult ?: false,
        backdropPath = backdrop_path ?: "",
        createdBy = "",
        credits = "",
        episodeRuntime = "",
        firstAirDate = release_date ?: "",
        genres = genre_ids.joinToString(",") { it.toString() } ?: "",
        homepage = "",
        id = id,
        images = "",
        inProduction = false,
        languages = "",
        lastAirDate = "",
        lastEpisodeToAir = "",
        name = title ?: "",
        networks = "",
        nextEpisodeToAir = "",
        numberOfEpisodes = 0,
        numberOfSeasons = 0,
        originCountry = "",
        originalLanguage = original_language ?: "",
        originalName = original_title ?: "",
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
        voteCount = vote_count ?: 0
    )
}

fun PartCollection.toMedia(): Media{
    return Media(
        id = id,
        title = title,
        posterPath = poster_path ?: "",
        backdropPath = backdrop_path ?: "",
        genreIds = genre_ids,
        voteAverage = vote_average,
        voteCount = vote_count,
        mediaType = media_type
    )
}

fun Cast.toCreditEntity(movieId: Int): CreditEntity {
    return CreditEntity(
        adult = adult ?: false,
        character = character ?: "",
        gender = gender ?: -1, //1 - nu, 2 - nam
        id = id,
        knownForDepartment = known_for_department ?: "",
        name = name ?: "",
        popularity = popularity ?: -1.0,
        profilePath = profile_path ?: "",
        department = "",
        job = "",
        type = "Cast",
        movieId = movieId.toString(),
        biography = "",
        birthday = "",
        deathday = "",
        homepage = "",
        placeOfBirth = "",
        externalIds = ""
    )
}

fun Crew.toCreditEntity(movieId: Int): CreditEntity {
    return CreditEntity(
        adult = adult ?: false,
        character = "",
        gender = gender ?: -1, //1 - nu, 2 - nam
        id = id,
        knownForDepartment = known_for_department ?: "",
        name = name ?: "",
        popularity = popularity ?: -1.0,
        profilePath = profile_path ?: "",
        department = department ?: "",
        job = job ?: "",
        type = "Crew",
        movieId = movieId.toString(),
        biography = "",
        birthday = "",
        deathday = "",
        homepage = "",
        placeOfBirth = "",
        externalIds = ""
    )
}

fun List<Cast>.toCreditEntity(movieId: Int): List<CreditEntity> {
    return this.filter { it.character != null && !it.character.contains("(voice)") }.map {
        it.toCreditEntity(movieId = movieId)
    }
}

fun Cast.toCredit(movieId: Int): Credit {
    return Credit(
        adult = adult ?: false,
        character = if (character == null) emptyList() else listOf(character),
        gender = gender ?: -1, //1 - nu, 2 - nam
        id = id,
        knownForDepartment = known_for_department ?: "",
        name = name ?: "",
        popularity = popularity ?: -1.0,
        profilePath = profile_path ?: "",
        department = "",
        job = "",
        type = "Cast",
        movieId = listOf(movieId.toString()),
        biography = "",
        birthday = "",
        deathday = "",
        homepage = "",
        placeOfBirth = "",
        externalIds = emptyList(),
        addedInFavorite = false
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

fun CreditDetail.toCreditEntity(type: String): CreditEntity {
    return CreditEntity(
        adult = adult ?: false,
        character = movie_credits?.cast?.filter { it?.character != null }
            ?.joinToString(",") { it?.character!! } ?: "",
        gender = gender ?: -1, //1 - nu, 2 - nam
        id = id,
        knownForDepartment = known_for_department ?: "",
        name = name ?: "",
        popularity = popularity ?: -1.0,
        profilePath = profile_path ?: "",
        department = "",
        job = "",
        type = type,
        movieId = if (type == "Cast") {
            movie_credits?.cast?.filter { it?.character != null }
                ?.joinToString(",") { it?.id.toString() } ?: ""
        } else {
            movie_credits?.crew?.filter { it?.job == "Director" }
                ?.joinToString(",") { it?.id.toString() } ?: ""
        },
        biography = biography ?: "",
        birthday = birthday ?: "",
        deathday = deathday.toString(),
        homepage = homepage.toString(),
        placeOfBirth = place_of_birth ?: "",
        externalIds = if (external_ids != null) (external_ids.facebook_id.toString() + "," + external_ids.twitter_id.toString() + "," + external_ids.instagram_id.toString()) else ""
    )
}

fun CreditDetail.toCredit(type: String): Credit {
    return Credit(
        adult = adult ?: false,
        character = movie_credits?.cast?.filter { it?.character != null  }
            ?.map { it?.character!! } ?: emptyList(),
        gender = gender ?: -1, //1 - nu, 2 - nam
        id = id,
        knownForDepartment = known_for_department ?: "",
        name = name ?: "",
        popularity = popularity ?: -1.0,
        profilePath = profile_path ?: "",
        department = "",
        job = "",
        type = type,
        movieId = movie_credits?.cast?.filter { it?.character != null  }
            ?.map { it?.id.toString() } ?: emptyList(),
        biography = biography ?: "",
        birthday = birthday ?: "",
        deathday = deathday.toString(),
        homepage = homepage.toString(),
        placeOfBirth = place_of_birth ?: "",
        externalIds = if (external_ids != null) listOf(
            external_ids.facebook_id.toString(),
            external_ids.twitter_id.toString(),
            external_ids.instagram_id.toString(),
            external_ids.wikidata_id.toString()
        ) else emptyList(),
        addedInFavorite = false
    )
}

fun CreditEntity.toCredit(): Credit {
    return Credit(
        adult = adult,
        character = character.split(","),
        gender = gender, //1 - nu, 2 - nam
        id = id,
        knownForDepartment = knownForDepartment,
        name = name,
        popularity = popularity,
        profilePath = profilePath,
        department = department,
        job = job,
        type = type,
        movieId = movieId.split(","),
        biography = biography,
        birthday = birthday,
        deathday = deathday.toString(),
        homepage = homepage,
        placeOfBirth = placeOfBirth,
        externalIds = externalIds.split(","),
        addedInFavorite = addedInFavorite == 1
    )
}

fun List<CreditEntity>.toListCredit(): List<Credit> {
    return this.map {
        it.toCredit()
    }
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

fun List<Movie>.toListMedia(): List<Media>{
    return this.map {
        it.toMedia()
    }
}