package hiendao.moviefinder.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.network.MovieNetworkMediator
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.util.Category
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MovieRepositoryImp @Inject constructor(
    private val movieDb: MovieDatabase,
    private val movieApi: MovieApi,
    private val movieDAO: MovieDAO
): MovieRepository {
    override fun getPopularMoviesPaged(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = {movieDAO.loadCategoryMoviesPaged(Category.POPULAR.name)}
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieNetworkMediator(
                movieDb, movieApi, Category.POPULAR
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getTopRatedMovies(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = {movieDAO.loadCategoryMoviesPaged(Category.TOP_RATED.name)}
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieNetworkMediator(
                movieDb, movieApi, Category.TOP_RATED
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getNowPlayingMovies(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = {movieDAO.loadCategoryMoviesPaged(Category.NOW_PLAYING.name)}
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieNetworkMediator(
                movieDb, movieApi, Category.NOW_PLAYING
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getUpcomingMovies(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = {movieDAO.loadCategoryMoviesPaged(Category.UPCOMING.name)}
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieNetworkMediator(
                movieDb, movieApi, Category.UPCOMING
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getTrendingDayMoviesPaged(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = {movieDAO.loadCategoryMoviesPaged(Category.TRENDING_DAY.name)}
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieNetworkMediator(
                movieDb, movieApi, Category.TRENDING_DAY
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getTrendingWeekMoviesPaged(): Flow<PagingData<MovieEntity>> {
        val pagingSourceFactory = {movieDAO.loadCategoryMoviesPaged(Category.TRENDING_WEEK.name)}
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = MovieNetworkMediator(
                movieDb, movieApi, Category.TRENDING_WEEK
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getPopularMovies(): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val localMovies = movieDAO.loadCategoryMovies(Category.POPULAR.name)

            val shouldJustLoadFromCache = localMovies.isNotEmpty()
            if(shouldJustLoadFromCache){
                emit(Resource.Success(
                    data = localMovies.map {
                        it.toMovie()
                    }
                ))
                emit(Resource.Loading(false))
            }

            val remoteMovies = try {
                movieApi.getPopularMovies(page = 1)
            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovies.results.let {movies ->
                val moviesResult = movies.map {
                    it.toMovie()
                }
                val moviesEntity = movies.mapIndexed { index, movieDTO ->
                    movieDTO.toMovieEntity(
                        category = Category.POPULAR,
                        index = (remoteMovies.page - 1) * 20 + index
                    )
                }

                movieDAO.upsertListMovie(moviesEntity)

                emit(Resource.Success(moviesResult))

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getTrendingDayMovies(): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val localMovies = movieDAO.loadCategoryMovies(Category.TRENDING_DAY.name)

            val shouldJustLoadFromCache = localMovies.isNotEmpty()
            if(shouldJustLoadFromCache){
                emit(Resource.Success(
                    data = localMovies.map {
                        it.toMovie()
                    }
                ))
                emit(Resource.Loading(false))
            }

            val remoteMovies = try {
                movieApi.getTrendingMovie(page = 1, timeWindow = "day")
            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovies.results.let {movies ->
                val moviesResult = movies.map {
                    it.toMovie()
                }
                val moviesEntity = movies.mapIndexed { index, movieDTO ->
                    movieDTO.toMovieEntity(
                        category = Category.TRENDING_DAY,
                        index = (remoteMovies.page - 1) * 20 + index
                    )
                }

                movieDAO.upsertListMovie(moviesEntity)

                emit(Resource.Success(moviesResult))

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getTrendingWeekMovies(): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val localMovies = movieDAO.loadCategoryMovies(Category.TRENDING_WEEK.name)

            val shouldJustLoadFromCache = localMovies.isNotEmpty()
            if(shouldJustLoadFromCache){
                emit(Resource.Success(
                    data = localMovies.map {
                        it.toMovie()
                    }
                ))
                emit(Resource.Loading(false))
            }

            val remoteMovies = try {
                movieApi.getTrendingMovie(page = 1, timeWindow = "week")
            }catch (e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovies.results.let {movies ->
                val moviesResult = movies.map {
                    it.toMovie()
                }
                val moviesEntity = movies.mapIndexed { index, movieDTO ->
                    movieDTO.toMovieEntity(
                        category = Category.TRENDING_WEEK,
                        index = (remoteMovies.page - 1) * 20 + index
                    )
                }

                movieDAO.upsertListMovie(moviesEntity)

                emit(Resource.Success(moviesResult))

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Flow<Resource<Movie>> {
        return flow {
            emit(Resource.Loading())

            val localMovie = movieDAO.getMovieWithId(movieId)

            if(localMovie == null){
                emit(Resource.Error("Couldn't found movie"))
                emit(Resource.Loading(false))
                return@flow
            }

            val isDetail = !(localMovie.runtime == 0 || localMovie.status == "" || localMovie.tagline == "")

            if(isDetail){
                emit(Resource.Success(
                    data = localMovie.toMovie()
                ))
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteMovie = try {
                movieApi.getMovieDetailInfo(
                    movieId
                )
            } catch(e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovie.let {movie ->

                remoteMovie.similar?.results?.let {listSimilar ->
                    listSimilar.mapIndexed { index, movieDTO ->
                        if(movieDTO.backdrop_path != null && movieDTO.poster_path != null){
                            val similarEntity = movieDTO.toMovieEntity(
                                category = Category.MOVIE,
                                index = (remoteMovie.similar.page - 1) * 10 + index
                            )
                            movieDAO.upsertMovie(similarEntity)
                        }
                    }
                }

                val movieResult = movie.toMovie()
                val movieEntity = movie.toMovieEntity()

                movieDAO.upsertMovie(movieEntity)

                emit(Resource.Success(
                    data = movieResult
                ))

                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getSimilarMovies(movieId: Int): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val localMovie = movieDAO.getMovieWithId(movieId)
            if(localMovie != null && localMovie.similar != ""){
                val similarIds = localMovie.similar.split(",")
                val similarMovies = mutableListOf<Movie>()
                similarIds.forEach {id ->
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
            } catch(e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMovie.let {movie ->

                remoteMovie.similar?.results?.let {listSimilar ->
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
    }

    override suspend fun getCollection(collectionId: Int): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())

            val remoteCollection = try {
                movieApi.getMovieCollection(
                    collectionId
                )
            } catch(e: Exception){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteCollection.parts.let {listPart ->
                listPart.forEach {
                    if(it.media_type == "movie"){
                        val movie = movieDAO.getMovieWithId(it.id)
                        if(movie == null){
                            movieDAO.upsertMovie(
                                it.toMovieEntity()
                            )
                        }
                    } else{
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

                emit(Resource.Success(
                    data = movies
                ))
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }
}