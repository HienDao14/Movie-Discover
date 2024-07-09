package hiendao.moviefinder.data.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.mapper.toMovieListResponse
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.util.Category
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MovieNetworkMediator @Inject constructor(
    private val movieDatabase: MovieDatabase,
    private val movieApi: MovieApi,
    private val category: Category
): RemoteMediator<Int, MovieEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null){
                        1
                    } else {
                        (lastItem.categoryIndex / state.config.pageSize) + 1
                    }
                }
            }
            val movies = when(category){
                Category.NOW_PLAYING -> movieApi.getNowPlayingMovies(page = loadKey).toMovieListResponse()
                Category.POPULAR -> movieApi.getPopularMovies(page = loadKey)
                Category.TOP_RATED -> movieApi.getTopRatedMovies(page = loadKey)
                Category.UPCOMING -> movieApi.getUpcomingMovies(page = loadKey).toMovieListResponse()
                Category.TRENDING_DAY -> movieApi.getTrendingMovie(timeWindow = "day", page = loadKey)
                Category.TRENDING_WEEK -> movieApi.getTrendingMovie(timeWindow = "week", page = loadKey)
                Category.MOVIE -> movieApi.getPopularMovies(page = loadKey)
            }
            movieDatabase.withTransaction {
                if(loadType == LoadType.REFRESH){
                    movieDatabase.dao.deleteAllMovieWithCategory(category.name)
                }
                val movieEntities = movies.results.map { it.toMovieEntity(category, movies.page) }

                movieDatabase.dao.upsertListMovie(movieEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = movies.results.isEmpty()
            )
        } catch (e: IOException){
            MediatorResult.Error(e)
        } catch (e : HttpException){
            MediatorResult.Error(e)
        }
    }
}