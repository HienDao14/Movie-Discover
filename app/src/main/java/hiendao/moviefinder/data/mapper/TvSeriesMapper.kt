package hiendao.moviefinder.data.mapper

import hiendao.moviefinder.data.local.model.TvSeriesEntity
import hiendao.moviefinder.data.network.tvseries.model.detail.TvSeriesDetailDTO
import hiendao.moviefinder.domain.model.TvSeries

fun TvSeriesDetailDTO.toTvSeriesEntity(): TvSeriesEntity {
    return TvSeriesEntity(
        adult = adult ?: false,
        backdropPath = backdrop_path ?: "",
        createdBy = created_by?.joinToString(",") { it?.name.toString() } ?: "",
        credits = credits?.cast?.filter { it?.character != null }
            ?.joinToString(",") { it?.character!! } ?: "",
        episodeRuntime = episode_run_time?.joinToString(",") { it.toString() } ?: "",
        firstAirDate = first_air_date ?: "",
        genres = genres?.joinToString(",") { it?.id.toString() } ?: "",
        homepage = homepage ?: "",
        id = id,
        images = (images?.posters?.take(10)?.joinToString(",") { it?.file_path.toString() }
            ?: "") + ";"
                + (images?.backdrops?.take(20)?.joinToString(",") { it?.file_path.toString() }
            ?: ""),
        inProduction = in_production ?: false,
        languages = languages?.joinToString(",") { it.toString() } ?: "",
        lastAirDate = last_air_date ?: "",
        lastEpisodeToAir = (last_episode_to_air?.id ?: "").toString(),
        name = name ?: "",
        networks = networks?.joinToString(",") { it?.name.toString() } ?: "",
        nextEpisodeToAir = (next_episode_to_air ?: "").toString(),
        numberOfEpisodes = number_of_episodes ?: 0,
        numberOfSeasons = number_of_seasons ?: 0,
        originCountry = origin_country?.joinToString(",") { it ?: "" } ?: "",
        originalLanguage = original_language ?: "",
        originalName = original_name ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = poster_path ?: "",
        productionCompanies = production_companies?.joinToString(",") { it?.name ?: "" } ?: "",
        productionCountries = production_countries?.joinToString(",") { it?.name ?: "" } ?: "",
        seasons = seasons?.joinToString(",") { it?.id.toString() } ?: "",
        spokenLanguages = spoken_languages?.joinToString(",") { it?.name ?: "" } ?: "",
        status = status ?: "",
        tagline = tagline ?: "",
        type = type ?: "",
        videos = videos?.results?.joinToString(",") { it?.key ?: "" } ?: "",
        voteAverage = vote_average ?: 0.0,
        voteCount = vote_count ?: 0
    )
}

fun TvSeriesEntity.toTvSeries(): TvSeries {
    return TvSeries(
        adult = adult,
        backdropPath = backdropPath,
        createdBy = createdBy,
        credits = credits.split(",").map { it.toInt() },
        episodeRuntime = episodeRuntime.split(","),
        firstAirDate = firstAirDate,
        genres = genres.split(",").map { it.toInt() },
        homepage = homepage,
        id = id,
        images = images.split(";"),
        inProduction = inProduction,
        languages = languages.split(","),
        lastAirDate = lastAirDate,
        lastEpisodeToAir = lastEpisodeToAir,
        name = name,
        networks = networks.split(","),
        nextEpisodeToAir = nextEpisodeToAir,
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        originCountry = originCountry.split(","),
        originalLanguage = originalLanguage,
        originalName = originalName,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath,
        productionCompanies = productionCompanies.split(","),
        productionCountries = productionCountries.split(","),
        seasons = seasons.split(",").map{ it.toInt() },
        spokenLanguages = spokenLanguages.split(","),
        status = status,
        tagline = tagline,
        type = type,
        videos = videos.split(","),
        voteAverage = voteAverage,
        voteCount = voteCount
    )
}

fun TvSeriesDetailDTO.toTvSeries(): TvSeries{
    return TvSeries(
        adult = adult ?: false,
        backdropPath = backdrop_path ?: "",
        createdBy = created_by?.joinToString(",") { it?.name.toString() } ?: "",
        credits = credits?.cast?.filter { it?.character != null }
            ?.map { it?.id ?: 0 } ?: emptyList(),
        episodeRuntime = episode_run_time?.map { it.toString() } ?: emptyList(),
        firstAirDate = first_air_date ?: "",
        genres = genres?.map { it?.id ?: 0 } ?: emptyList(),
        homepage = homepage ?: "",
        id = id,
        images = images?.posters?.map { it?.file_path ?: "" }?.plus(images.backdrops?.map { it?.file_path ?: "" } ?: emptyList()) ?: emptyList(),
        inProduction = in_production ?: false,
        languages = languages?.map { it ?: "" } ?: emptyList(),
        lastAirDate = last_air_date ?: "",
        lastEpisodeToAir = (last_episode_to_air?.id ?: "").toString(),
        name = name ?: "",
        networks = networks?.map { it?.name.toString() } ?: emptyList(),
        nextEpisodeToAir = (next_episode_to_air ?: "").toString(),
        numberOfEpisodes = number_of_episodes ?: 0,
        numberOfSeasons = number_of_seasons ?: 0,
        originCountry = origin_country?.map { it ?: "" } ?: emptyList(),
        originalLanguage = original_language ?: "",
        originalName = original_name ?: "",
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = poster_path ?: "",
        productionCompanies = production_companies?.map { it?.name ?: "" } ?: emptyList(),
        productionCountries = production_countries?.map { it?.name ?: "" } ?: emptyList(),
        seasons = seasons?.map { it?.id ?: 0 } ?: emptyList(),
        spokenLanguages = spoken_languages?.map { it?.name ?: "" } ?: emptyList(),
        status = status ?: "",
        tagline = tagline ?: "",
        type = type ?: "",
        videos = videos?.results?.map { it?.key ?: "" } ?: emptyList(),
        voteAverage = vote_average ?: 0.0,
        voteCount = vote_count ?: 0
    )
}