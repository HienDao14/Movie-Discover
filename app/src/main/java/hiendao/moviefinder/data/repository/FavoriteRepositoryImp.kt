package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.dao.CreditDAO
import hiendao.moviefinder.data.local.dao.MovieDAO
import hiendao.moviefinder.data.local.dao.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toListCredit
import hiendao.moviefinder.data.mapper.entityToListMedia
import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.repository.FavoriteRepository
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FavoriteRepositoryImp @Inject constructor(
    private val movieDAO: MovieDAO,
    private val creditDAO: CreditDAO,
    private val tvSeriesDAO: TvSeriesDAO
): FavoriteRepository {

    override suspend fun getMovieFavorite(page: Int): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val movies = movieDAO.getFavoriteMovies(number = (page - 1) * 20)
                emit(Resource.Success(movies.entityToListMedia()))
                emit(Resource.Loading(false))
            } catch (e : Exception){
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getCreditFavorite(page: Int): Flow<Resource<List<Credit>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val credits = creditDAO.getFavoriteCredits(number = (page - 1) * 20)
                emit(Resource.Success(credits.toListCredit()))
                emit(Resource.Loading(false))
            } catch (e : Exception){
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getTvSeriesFavorite(page: Int): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val series = tvSeriesDAO.getFavoriteTvSeries(number = (page - 1) * 20)
                emit(Resource.Success(series.entityToListMedia()))
                emit(Resource.Loading(false))
            } catch (e : Exception){
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }
    }
}