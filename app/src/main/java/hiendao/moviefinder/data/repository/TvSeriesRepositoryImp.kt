package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toMedias
import hiendao.moviefinder.data.mapper.toListTvSeries
import hiendao.moviefinder.data.mapper.toTvSeries
import hiendao.moviefinder.data.mapper.toTvSeriesEntity
import hiendao.moviefinder.data.network.tvseries.TvSeriesApi
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TvSeriesRepositoryImp @Inject constructor(
    private val tvSeriesApi: TvSeriesApi,
    private val tvSeriesDAO: TvSeriesDAO
) : TvSeriesRepository {

    override suspend fun getPopularTvSeries(page: Int): Flow<Resource<List<TvSeries>>> {
        return flow {
            emit(Resource.Loading())

            val remoteTvSeries = tvSeriesApi.getPopularTvSeries(page = page).results
            remoteTvSeries.forEach { dto ->

                val entity = tvSeriesDAO.getTvSeriesById(dto.id)
                val favorite = entity?.addedToFavorite
                val date = entity?.addedInFavoriteDate
                tvSeriesDAO.insertTvSeries(dto.toTvSeriesEntity(favorite ?: 0, date ?: ""))
            }

            val listTvSeries = remoteTvSeries.toListTvSeries()
            emit(Resource.Success(listTvSeries))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getTrendingDayTvSeries(): Flow<Resource<List<TvSeries>>> {
        return flow {
            emit(Resource.Loading())


        }
    }

    override suspend fun discoverTvSeries(
        firstAirDateLte: String,
        page: Int,
        withGenres: String?,
        sortBy: String,
        voteCountGte: Float?
    ): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            val remoteTvSeries = try {
                tvSeriesApi.getTvSeriesDiscover(
                    firstAirDateLte, page, withGenres, sortBy, voteCountGte
                ).results
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteTvSeries.forEach { dto ->
                val entity = tvSeriesDAO.getTvSeriesById(dto.id)
                val favorite = entity?.addedToFavorite
                val date = entity?.addedInFavoriteDate
                tvSeriesDAO.insertTvSeries(dto.toTvSeriesEntity(favorite ?: 0, date ?: ""))
            }

            val listMedia = remoteTvSeries.toMedias()

            emit(Resource.Success(listMedia))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getTvSeriesDetail(isRefresh: Boolean, id: Int): Flow<Resource<TvSeries>> {
        return flow {
            emit(Resource.Loading())

            if (isRefresh) {
                val remoteSeries = try {
                    tvSeriesApi.getTvSeriesDetail(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error(e.message))
                    emit(Resource.Loading(false))
                    return@flow
                }
                val entity = tvSeriesDAO.getTvSeriesById(id)
                entity?.let {
                    val favorite = it.addedToFavorite
                    val date = it.addedInFavoriteDate
                    tvSeriesDAO.insertTvSeries(remoteSeries.toTvSeriesEntity(favorite, date))
                } ?: tvSeriesDAO.insertTvSeries(remoteSeries.toTvSeriesEntity(0, ""))

                emit(Resource.Success(remoteSeries.toTvSeries()))
                emit(Resource.Loading(false))
                return@flow
            } else {
                //Call Local first -> Call api % insert to Local

                val localSeries = tvSeriesDAO.getTvSeriesById(id)
                if (localSeries != null && localSeries.numberOfSeasons != 0 && localSeries.numberOfEpisodes != 0) {
                    emit(Resource.Success(localSeries.toTvSeries()))
                    emit(Resource.Loading(false))
                    return@flow
                }

                val remoteSeries = try {
                    tvSeriesApi.getTvSeriesDetail(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(Resource.Error(e.message))
                    emit(Resource.Loading(false))
                    return@flow
                }

                val entity = tvSeriesDAO.getTvSeriesById(id)
                entity?.let {
                    val favorite = it.addedToFavorite
                    val date = it.addedInFavoriteDate
                    tvSeriesDAO.insertTvSeries(remoteSeries.toTvSeriesEntity(favorite, date))
                } ?: tvSeriesDAO.insertTvSeries(remoteSeries.toTvSeriesEntity(0, ""))

                emit(Resource.Success(remoteSeries.toTvSeries()))
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }
}