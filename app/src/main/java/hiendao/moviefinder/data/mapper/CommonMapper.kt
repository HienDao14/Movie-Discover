package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.CreditEntity
import hiendao.moviefinder.data.network.movie.model.collection.PartCollection
import hiendao.moviefinder.data.network.movie.model.credit.Cast
import hiendao.moviefinder.data.network.movie.model.credit.Crew
import hiendao.moviefinder.data.network.movie.model.credit.detail.CreditDetail
import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media


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

fun PartCollection.toMedia(): Media {
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

fun List<CreditEntity>.toListCredit(): List<Credit> {
    return this.map {
        it.toCredit()
    }
}

fun List<Cast>.toCreditEntity(movieId: Int): List<CreditEntity> {
    return this.filter { it.character != null && !it.character.contains("(voice)") }.map {
        it.toCreditEntity(movieId = movieId)
    }
}