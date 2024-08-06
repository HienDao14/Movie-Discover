package hiendao.moviefinder.data.repository

import androidx.paging.ExperimentalPagingApi
import hiendao.moviefinder.data.local.CreditDAO
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.local.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toListMovie
import hiendao.moviefinder.data.mapper.toListMovieEntity
import hiendao.moviefinder.data.mapper.toMedia
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.mapper.toTvSeriesEntity
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.util.Category
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieRepositoryImp @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDAO: MovieDAO,
    private val creditDAO: CreditDAO,
    private val tvSeriesDAO: TvSeriesDAO
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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.TOP_RATED.name)) it.category
                                else it.category + Category.TOP_RATED.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.TOP_RATED.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                    return@flow
                }
            }

            val localMovies =
                movieDAO.loadCategoryMovies("%${Category.TOP_RATED.name}%", (page - 1) * 20)

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
                movies.forEachIndexed { index, movie ->
                    val entity = movieDAO.getMovieWithId(movie.id)
                    entity?.let {
                        val category =
                            if (it.category.contains(Category.TOP_RATED.name)) it.category
                            else it.category + Category.TOP_RATED.name
                        movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = category,
                                index = (remoteMovies.page - 1) * 20 + index,
                                favorite = it.addedToFavorite,
                                favoriteDate = it.addedInFavoriteDate
                            )
                        )
                    } ?: movieDAO.upsertMovie(
                        movie.toMovieEntity(
                            category = Category.TOP_RATED.name,
                            index = (remoteMovies.page - 1) * 20 + index
                        )
                    )
                }

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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.POPULAR.name)) it.category
                                else it.category + Category.POPULAR.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.POPULAR.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            } else {
                val localMovies =
                    movieDAO.loadCategoryMovies("%${Category.POPULAR.name}%", (page - 1) * 20)

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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.POPULAR.name)) it.category
                                else it.category + Category.POPULAR.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.POPULAR.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }

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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.TRENDING_DAY.name)) it.category
                                else it.category + Category.TRENDING_DAY.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.TRENDING_DAY.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            } else {
                val localMovies =
                    movieDAO.loadCategoryMovies("%${Category.TRENDING_DAY.name}%", (page - 1) * 20)

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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.TRENDING_DAY.name)) it.category
                                else it.category + Category.TRENDING_DAY.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.TRENDING_DAY.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }
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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.TRENDING_WEEK.name)) it.category
                                else it.category + Category.TRENDING_WEEK.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.TRENDING_WEEK.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }

                    emit(Resource.Success(moviesResult))

                    emit(Resource.Loading(false))
                }
            } else {

                val localMovies =
                    movieDAO.loadCategoryMovies("%${Category.TRENDING_WEEK.name}%", (page - 1) * 20)

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
                    movies.forEachIndexed { index, movie ->
                        val entity = movieDAO.getMovieWithId(movie.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.TRENDING_WEEK.name)) it.category
                                else it.category + Category.TRENDING_WEEK.name
                            movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = category,
                                    index = (remoteMovies.page - 1) * 20 + index,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movie.toMovieEntity(
                                category = Category.TRENDING_WEEK.name,
                                index = (remoteMovies.page - 1) * 20 + index
                            )
                        )
                    }

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
                            val entity = movieDAO.getMovieWithId(movieDTO.id)
                            entity?.let {
                                val category =
                                    if (it.category.contains(Category.MOVIE.name)) it.category
                                    else it.category + Category.MOVIE.name
                                movieDAO.upsertMovie(
                                    movie.toMovieEntity(
                                        category = category,
                                        index = -1,
                                        favorite = it.addedToFavorite,
                                        favoriteDate = it.addedInFavoriteDate
                                    )
                                )
                            } ?: movieDAO.upsertMovie(
                                movie.toMovieEntity(
                                    category = Category.MOVIE.name,
                                    index = -1
                                )
                            )
                        }
                    }
                }

                val movieResult = movie.toMovie()

                emit(
                    Resource.Success(
                        data = movieResult
                    )
                )

                emit(Resource.Loading(false))
                return@flow
            }
        }
    }

    override suspend fun getSimilarMovies(movieId: Int): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            val localMovie = movieDAO.getMovieWithId(movieId)
            if (localMovie != null && localMovie.similar != "") {
                val similarIds = localMovie.similar.split(",")
                val similarMovies = mutableListOf<Media>()
                similarIds.forEach { id ->
                    val movie = movieDAO.getMovieWithId(id.toInt())
                    movie?.let {
                        similarMovies.add(it.toMovie().toMedia())
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

                    if (movieDTO.backdrop_path != null && movieDTO.poster_path != null) {
                        val entity = movieDAO.getMovieWithId(movieDTO.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.MOVIE.name)) it.category
                                else it.category + Category.MOVIE.name
                            movieDAO.upsertMovie(
                                movieDTO.toMovieEntity(
                                    category = category,
                                    index = -1,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            movieDTO.toMovieEntity(
                                category = Category.MOVIE.name,
                                index = -1
                            )
                        )
                    }
                }
                val similarMovies = listSimilar.map { it.toMovie().toMedia() }
                emit(Resource.Success(similarMovies))
                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Error("Couldn't load data"))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getCollection(collectionId: Int): Flow<Resource<List<Media>>> {
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
                        val tvSeries = tvSeriesDAO.getTvSeriesById(it.id)
                        if(tvSeries == null){
                            tvSeriesDAO.insertTvSeries(
                                it.toTvSeriesEntity()
                            )
                        }
                    }
                }

                val movies = listPart
                    .map {
                        it.toMedia()
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
                            movieCredits.cast?.toListMovieEntity(Category.MOVIE.name, -1)
                                ?.toListMovie()
                                ?.let {
                                    movies = it
                                }
                        } else {
                            movieCredits.crew?.toListMovieEntity(Category.MOVIE.name, -1)
                                ?.toListMovie()
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
    ): Flow<Resource<List<Media>>> {
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
                    it.toMedia()
                }
                movies.forEach { movieDTO ->
                    val entity = movieDAO.getMovieWithId(movieDTO.id)
                    entity?.let {
                        val category =
                            if (it.category.contains(Category.MOVIE.name)) it.category
                            else it.category + Category.MOVIE.name
                        movieDAO.upsertMovie(
                            movieDTO.toMovieEntity(
                                category = category,
                                index = -1,
                                favorite = it.addedToFavorite,
                                favoriteDate = it.addedInFavoriteDate
                            )
                        )
                    } ?: movieDAO.upsertMovie(
                        movieDTO.toMovieEntity(
                            category = Category.MOVIE.name,
                            index = -1
                        )
                    )
                }

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