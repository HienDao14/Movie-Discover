package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.dao.CreditDAO
import hiendao.moviefinder.data.local.dao.MovieDAO
import hiendao.moviefinder.data.local.dao.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toCredit
import hiendao.moviefinder.data.mapper.toCreditEntity
import hiendao.moviefinder.data.mapper.toListCredit
import hiendao.moviefinder.data.mapper.toMedia
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.mapper.toTvSeries
import hiendao.moviefinder.data.mapper.toTvSeriesEntity
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.data.network.tvseries.TvSeriesApi
import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.util.Category
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommonRepositoryImp @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDAO: MovieDAO,
    private val tvSeriesApi: TvSeriesApi,
    private val tvSeriesDAO: TvSeriesDAO,
    private val creditDAO: CreditDAO
) : CommonRepository {
    override suspend fun getCredits(type: String, id: Int): Flow<Resource<List<Credit>>> {
        return flow {
            emit(Resource.Loading())

            val movie = movieDAO.getMovieWithId(id)
            val tvSeries = tvSeriesDAO.getTvSeriesById(id)
            val credits = if(type == "Movie") movie?.credits else tvSeries?.credits
            val listCreditId = mutableListOf<Int>()
            val listCredit = mutableListOf<Credit>()
            if (credits.isNullOrEmpty()) {
                val remoteCredits = if(type == "Movie") movieApi.getCredits(id) else tvSeriesApi.getCredits(id)
                if (!remoteCredits.cast.isNullOrEmpty()) {
                    remoteCredits.cast.sortedByDescending { it.popularity }
                        .filter { it.character != null }.take(20).forEach { cast ->
                        val localCredit = creditDAO.getCredit(cast.id)
                        if (localCredit != null) {
                            if(!localCredit.movieId.contains(id.toString())){
                                val movieIds = localCredit.movieId + ",${id}"
                                val characters = localCredit.character + ",${cast.character}"
                                creditDAO.insertCredit(characters, movieIds, cast.id)
                            }
                        } else {
                            val creditEntity = cast.toCreditEntity(id)

                            creditDAO.upsertCredit(creditEntity)
                            listCredit.add(creditEntity.toCredit())
                            listCreditId.add(cast.id)
                        }
                    }
                }

                if (!remoteCredits.crew.isNullOrEmpty()) {
                    remoteCredits.crew.filter {
                        it.job != null && it.job == "Director"
                    }.forEach { crew ->
                        val creditEntity = crew.toCreditEntity(id)
                        creditDAO.upsertCredit(creditEntity)
                        listCredit.add(creditEntity.toCredit())
                        listCreditId.add(crew.id)
                    }
                }

                if(type == "Movie"){
                    movieDAO.updateCredits(
                        credit = listCreditId.joinToString(",") { it.toString() },
                        movieId = id
                    )
                } else {
                    tvSeriesDAO.updateCredits(
                        credit = listCreditId.joinToString(",") { it.toString() },
                        id = id
                    )
                }
            }

            val localCredits = creditDAO.getCredits("%${id}%")
            emit(Resource.Success(data = localCredits.toListCredit()))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getCreditDetail(personId: Int): Flow<Resource<Credit>> {
        return flow {
            emit(Resource.Loading())

            val localCredit = creditDAO.getCredit(personId)
            if (localCredit == null || (localCredit.birthday == "" && localCredit.placeOfBirth == "")) {
                val remoteCredit = movieApi.getCreditDetail(personId = personId)
                val type = if (remoteCredit.known_for_department == "Directing") "Crew" else "Cast"
                remoteCredit.movie_credits?.let {
                    if (type == "Cast") {
                        it.cast?.filter { it?.character != null }?.forEach {
                            it?.let { movie ->
                                movieDAO.insertMovie(movie.toMovieEntity(Category.MOVIE, 1))
                            }
                        }
                    } else {
                        it.crew?.filter { it?.job == "Director" }?.forEach {
                            it?.let { movie ->
                                movieDAO.insertMovie(movie.toMovieEntity(Category.MOVIE, 1))
                            }
                        }
                    }
                }
                creditDAO.upsertCredit(remoteCredit.toCreditEntity(type))
                emit(Resource.Success(data = remoteCredit.toCredit(type)))
                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Success(data = localCredit.toCredit()))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun changeFavoriteCredit(
        favorite: Int,
        addedDate: String,
        personId: Int
    ): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            try {
                creditDAO.changeFavoriteCredit(favorite = favorite, addedDate = addedDate, creditId = personId)
                emit(Resource.Success(data = true))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }

    override suspend fun getSimilarMovies(type: String, id: Int): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            val localMovie = movieDAO.getMovieWithId(id)
            val localSeries = tvSeriesDAO.getTvSeriesById(id)

            if (type == "Movie" && localMovie != null && localMovie.similar != "") {
                val similarIds = localMovie.similar.split(",")
                val similarMedias = mutableListOf<Media>()
                similarIds.forEach { id ->
                    val movie = movieDAO.getMovieWithId(id.toInt())
                    movie?.let {
                        similarMedias.add(it.toMovie().toMedia())
                    }
                }

                emit(Resource.Success(similarMedias))
                emit(Resource.Loading(false))
                return@flow
            }

            if (type != "Movie" && localSeries != null && localSeries.similar != "") {
                val similarIds = localSeries.similar.split(",")
                val similarMedias = mutableListOf<Media>()
                similarIds.forEach { id ->
                    val series = tvSeriesDAO.getTvSeriesById(id.toInt())
                    series?.let {
                        similarMedias.add(it.toTvSeries().toMedia())
                    }
                }

                emit(Resource.Success(similarMedias))
                emit(Resource.Loading(false))
                return@flow
            }

            if(type == "Movie"){
                val remoteMovie = try {
                    movieApi.getMovieDetailInfo(
                        id
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovie.similar?.results?.let { listSimilar ->
                    listSimilar.mapIndexed { _, movieDTO ->

                        if (movieDTO.backdrop_path != null && movieDTO.poster_path != null) {
                            val entity = movieDAO.getMovieWithId(movieDTO.id)
                            entity?.let {
                                val category =
                                    if (it.category.contains(Category.MOVIE.name)) it.category
                                    else it.category + Category.MOVIE.name
                                movieDAO.upsertMovie(
                                    movieDTO.toMovieEntity(
                                        category = category,
                                        index = it.categoryIndex,
                                        favorite = it.addedToFavorite,
                                        favoriteDate = it.addedInFavoriteDate,
                                        categoryAddedDate = it.categoryDateAdded
                                    )
                                )
                            } ?: movieDAO.upsertMovie(
                                movieDTO.toMovieEntity(
                                    category = Category.MOVIE.name,
                                    index = 10000,
                                    categoryAddedDate = "empty"
                                )
                            )
                        }
                    }
                    val similarMedias = listSimilar.map { it.toMovie().toMedia() }
                    emit(Resource.Success(similarMedias))
                    emit(Resource.Loading(false))
                    return@flow
                }
            }

            if(type != "Movie"){
                val remoteSeries = try {
                    tvSeriesApi.getTvSeriesDetail(
                        id
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteSeries.similar?.results?.let { listSimilar ->
                    listSimilar.mapIndexed { _, seriesDTO ->

                        if (seriesDTO.backdrop_path != null && seriesDTO.poster_path != null) {
                            val entity = movieDAO.getMovieWithId(seriesDTO.id)
                            entity?.let {
                                tvSeriesDAO.insertTvSeries(
                                    seriesDTO.toTvSeriesEntity(
                                        favorite = it.addedToFavorite,
                                        favoriteDate = it.addedInFavoriteDate
                                    )
                                )
                            } ?: tvSeriesDAO.insertTvSeries(
                                seriesDTO.toTvSeriesEntity(
                                    favorite = 0,
                                    favoriteDate = ""
                                )
                            )
                        }
                    }
                    val similarMedias = listSimilar.map { it.toMedia() }
                    emit(Resource.Success(similarMedias))
                    emit(Resource.Loading(false))
                    return@flow
                }
            }

            emit(Resource.Error("Couldn't load data"))
            emit(Resource.Loading(false))
            return@flow
        }
    }
}