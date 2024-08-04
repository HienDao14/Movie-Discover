package hiendao.moviefinder.data.repository

import androidx.paging.ExperimentalPagingApi
import hiendao.moviefinder.data.local.CreditDAO
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.mapper.toListMovie
import hiendao.moviefinder.data.mapper.toListMovieEntity
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.util.Category
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImp @Inject constructor(
    private val movieDb: MovieDatabase,
    private val movieApi: MovieApi,
    private val movieDAO: MovieDAO,
    private val creditDAO: CreditDAO
) : MovieRepository {

    override suspend fun getTopRatedMovies(
        page: Int,
        isRefresh: Boolean,
        shouldCallNetwork: Boolean
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            if (isRefresh || shouldCallNetwork) {
                val remoteMovies = try {
                    movieApi.getTopRatedMovies(page = page)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.TOP_RATED,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                    return@flow
                }
            }

            val localMovies = movieDAO.loadCategoryMovies(Category.TOP_RATED.name, (page - 1) * 20)

            val shouldJustLoadFromCache = localMovies.isNotEmpty()
            if (shouldJustLoadFromCache) {
                emit(Resource.Success(
                    data = localMovies.map {
                        it.toMovie()
                    }
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteMovies = try {
                movieApi.getTopRatedMovies(page = page)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovies.results.let { movies ->
                val moviesResult = movies.map {
                    it.toMovie()
                }
                val moviesEntity = movies.mapIndexed { index, movieDTO ->
                    movieDTO.toMovieEntity(
                        category = Category.TOP_RATED,
                        index = (remoteMovies.page - 1) * 20 + index
                    )
                }

                movieDAO.insertListMovie(moviesEntity)

                emit(Resource.Success(moviesResult))

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getPopularMovies(
        page: Int,
        isRefresh: Boolean
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            if (isRefresh) {
                val remoteMovies = try {
                    movieApi.getPopularMovies(page = page)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.POPULAR,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            } else {
                val localMovies =
                    movieDAO.loadCategoryMovies(Category.POPULAR.name, (page - 1) * 20)

                val shouldJustLoadFromCache = localMovies.isNotEmpty()
                if (shouldJustLoadFromCache) {
                    emit(Resource.Success(
                        data = localMovies.map {
                            it.toMovie()
                        }
                    ))
                    emit(Resource.Loading(false))
                    return@flow
                }

                val remoteMovies = try {
                    movieApi.getPopularMovies(page = page)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.POPULAR,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            }
        }
    }

    override suspend fun getTrendingDayMovies(
        page: Int,
        isRefresh: Boolean
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            if (isRefresh) {
                val remoteMovies = try {
                    movieApi.getTrendingMovie(page = page, timeWindow = "day")
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.TRENDING_DAY,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            } else {
                val localMovies =
                    movieDAO.loadCategoryMovies(Category.TRENDING_DAY.name, (page - 1) * 20)

                if (localMovies.isNotEmpty()) {
                    emit(Resource.Success(
                        data = localMovies.map {
                            it.toMovie()
                        }
                    ))
                    emit(Resource.Loading(false))
                    return@flow
                }

                val remoteMovies = try {
                    movieApi.getTrendingMovie(page = page, timeWindow = "day")
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.TRENDING_DAY,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            }
        }
    }

    override suspend fun getTrendingWeekMovies(
        page: Int,
        isRefresh: Boolean,
        shouldCallNetwork: Boolean
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            if (isRefresh || shouldCallNetwork) {
                val remoteMovies = try {
                    movieApi.getTrendingMovie(page = page, timeWindow = "week")
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.TRENDING_WEEK,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            } else {

                val localMovies =
                    movieDAO.loadCategoryMovies(Category.TRENDING_WEEK.name, (page - 1) * 20)

                val shouldJustLoadFromCache = localMovies.isNotEmpty()
                if (shouldJustLoadFromCache) {
                    emit(Resource.Success(
                        data = localMovies.map {
                            it.toMovie()
                        }
                    ))
                    emit(Resource.Loading(false))
                    return@flow
                }

                val remoteMovies = try {
                    movieApi.getTrendingMovie(page = page, timeWindow = "week")
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                }

                remoteMovies.results.let { movies ->
                    val moviesResult = movies.map {
                        it.toMovie()
                    }
                    val moviesEntity = movies.mapIndexed { index, movieDTO ->
                        movieDTO.toMovieEntity(
                            category = Category.TRENDING_WEEK,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    }

                    movieDAO.insertListMovie(moviesEntity)

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            }

        }
    }

    override suspend fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> {
        return flow {
            emit(Resource.Loading())

            val localMovie = movieDAO.getMovieWithId(movieId)

            if (localMovie == null) {
                emit(Resource.Error("Couldn't found movie"))
                emit(Resource.Loading(false))
                return@flow
            }

            val isDetail =
                !(localMovie.runtime == 0 || localMovie.status == "" || localMovie.images.isEmpty() || localMovie.videos.isNullOrEmpty())

            if (isDetail) {
                emit(
                    Resource.Success(
                        data = localMovie.toMovie()
                    )
                )
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteMovie = try {
                movieApi.getMovieDetailInfo(
                    movieId
                )
            } catch (e: Exception) {
                println("movie screen: ${e.message}")
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovie.let { movie ->
                remoteMovie.similar?.results?.let { listSimilar ->
                    listSimilar.mapIndexed { index, movieDTO ->
                        if (movieDTO.backdrop_path != null && movieDTO.poster_path != null) {
                            val similarEntity = movieDTO.toMovieEntity(
                                category = Category.MOVIE,
                                index = (remoteMovie.similar.page - 1) * 10 + index
                            )
                            movieDAO.upsertMovie(similarEntity)
                        }
                    }
                }

                val movieEntity = movie.toMovieEntity(
                    category = localMovie.category,
                    index = localMovie.categoryIndex
                )
                val movieResult = movieEntity.toMovie()

                movieDAO.upsertMovie(movieEntity)

                emit(
                    Resource.Success(
                        data = movieResult
                    )
                )

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getSimilarMovies(movieId: Int): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val localMovie = movieDAO.getMovieWithId(movieId)
            if (localMovie != null && localMovie.similar != "") {
                val similarIds = localMovie.similar.split(",")
                val similarMovies = mutableListOf<Movie>()
                similarIds.forEach { id ->
                    val movie = movieDAO.getMovieWithId(id.toInt())
                    movie?.let {
                        similarMovies.add(it.toMovie())
                    }
                }

                emit(Resource.Success(similarMovies))
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteMovie = try {
                movieApi.getMovieDetailInfo(
                    movieId
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovie.similar?.results?.let { listSimilar ->
                listSimilar.mapIndexed { index, movieDTO ->
                    val similarEntity = movieDTO.toMovieEntity(
                        category = Category.MOVIE,
                        index = (remoteMovie.similar.page - 1) * 10 + index
                    )
                    movieDAO.upsertMovie(similarEntity)
                }
                val similarMovies = listSimilar.map { it.toMovie() }
                emit(Resource.Success(similarMovies))
                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Error("Couldn't load data"))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getCollection(collectionId: Int): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val remoteCollection = try {
                movieApi.getMovieCollection(
                    collectionId
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteCollection.parts.let { listPart ->
                listPart.forEach {
                    if (it.media_type == "movie") {
                        val movie = movieDAO.getMovieWithId(it.id)
                        if (movie == null) {
                            movieDAO.upsertMovie(
                                it.toMovieEntity()
                            )
                        }
                    } else {
                        //Tv Series
                    }
                }

                val movies = listPart
                    .filter {
                        it.media_type == "movie"
                    }
                    .map {
                        it.toMovie()
                    }

                emit(
                    Resource.Success(
                        data = movies
                    )
                )
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }

    override suspend fun getMoviesWithCreditId(creditId: Int): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val localCredit = creditDAO.getCredit(creditId)
                if (localCredit != null && localCredit.movieId.isNotEmpty()) {
                    var movies = emptyList<Movie>()
                    val movie = movieDAO.getMoviesInListId(localCredit.movieId)
                    if (movie.isNotEmpty()) {
                        emit(Resource.Success(movie.toListMovie()))
                        emit(Resource.Loading(false))
                        return@flow
                    } else {
                        val movieCredits = movieApi.getMovieCredits(creditId)
                        if (localCredit.type == "Cast") {
                            movieCredits.cast?.toListMovieEntity(Category.MOVIE, 1)?.toListMovie()
                                ?.let {
                                    movies = it
                                }

                        } else {
                            movieCredits.crew?.toListMovieEntity(Category.MOVIE, 1)?.toListMovie()
                                ?.let {
                                    movies = it
                                }
                        }
                        if (movies.isNotEmpty()) {
                            emit(Resource.Success(movies))
                            emit(Resource.Loading(false))
                            return@flow
                        } else {
                            emit(Resource.Error("Couldn't load data"))
                            emit(Resource.Loading(false))
                            return@flow
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

        }
    }

    override suspend fun discoverMovies(
        releaseDateLte: String,
        page: Int,
        withGenres: String?,
        sortBy: String,
        voteCountGte: Float?
    ): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())
            val remoteMovies = try {
                movieApi.getMovieDiscover(
                    releaseDateLte = releaseDateLte,
                    page = page,
                    withGenres = withGenres,
                    sortBy = sortBy,
                    voteCountGte = voteCountGte
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovies.results.let { movies ->
                val moviesResult = movies.map {
                    it.toMovie()
                }
                val moviesEntity = movies.map { movieDTO ->
                    movieDTO.toMovieEntity(
                        category = Category.MOVIE,
                        index = -1
                    )
                }

                movieDAO.insertListMovie(moviesEntity)

                emit(Resource.Success(moviesResult))

                emit(Resource.Loading(false))
                return@flow
            }
        }
    }

    override suspend fun changeFavoriteMovie(
        favorite: Int,
        addedDate: String,
        movieId: Int
    ): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            try {
                movieDAO.changeFavorite(favorite, addedDate, movieId)
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
}