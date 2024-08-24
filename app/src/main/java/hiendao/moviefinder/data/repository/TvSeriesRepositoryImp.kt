package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.dao.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toMedia
import hiendao.moviefinder.data.mapper.toMedias
import hiendao.moviefinder.data.mapper.toTvSeries
import hiendao.moviefinder.data.mapper.toTvSeriesEntity
import hiendao.moviefinder.data.network.tvseries.TvSeriesApi
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.util.Category
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TvSeriesRepositoryImp @Inject constructor(
    private val tvSeriesApi: TvSeriesApi,
    private val tvSeriesDAO: TvSeriesDAO
) : TvSeriesRepository {

    override suspend fun getPopularTvSeriesRemote(page: Int): List<Media> {
        val remoteSeries =
            try {
                tvSeriesApi.getPopularTvSeries(page = page)
            } catch (e : Exception){
                e.printStackTrace()
                return emptyList()
            }

        remoteSeries.results.let {list ->
            val result = list.map {
                it.toMedia()
            }

            list.forEach {series ->
                val entity = tvSeriesDAO.getTvSeriesById(series.id)
                entity?.let {
                    val category = if(it.category.contains("POPULAR")) it.category else
                        it.category + Category.POPULAR.name
                    tvSeriesDAO.insertTvSeries(
                        series.toTvSeriesEntity(
                            category = category,
                            favorite = it.addedToFavorite,
                            favoriteDate = it.addedInFavoriteDate
                        )
                    )
                } ?: tvSeriesDAO.insertTvSeries(
                    series.toTvSeriesEntity(
                        favorite = 0, favoriteDate = "", category = Category.POPULAR.name
                    )
                )
            }

            return result
        }
    }

    override suspend fun getPopularTvSeries(page: Int, isRefresh: Boolean, shouldCallNetwork: Boolean): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            if(isRefresh || shouldCallNetwork){
                val result = getPopularTvSeriesRemote(page)
                if(result.isEmpty()){
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                } else {
                    emit(Resource.Success(result))
                    emit(Resource.Loading(false))
                    return@flow
                }
            }

            val local = tvSeriesDAO.loadCategoryTvSeries("$${Category.POPULAR.name}%", number = (page - 1) * 20)
            if(local.isNotEmpty()){
                emit(Resource.Success(data = local.map {
                    it.toMedia()
                }))
                emit(Resource.Loading(false))
                return@flow
            }

            val result = getPopularTvSeriesRemote(page)
            if(result.isEmpty()){
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            } else {
                emit(Resource.Success(result))
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }

    override suspend fun getTopRatedTvSeriesRemote(page: Int): List<Media> {
        val remoteSeries =
            try {
                tvSeriesApi.getTopRatedTvSeries(page = page)
            } catch (e : Exception){
                e.printStackTrace()
                return emptyList()
            }

        remoteSeries.results.let {list ->
            val result = list.map {
                it.toMedia()
            }

            list.forEach {series ->
                val entity = tvSeriesDAO.getTvSeriesById(series.id)
                entity?.let {
                    val category = if(it.category.contains("TOP_RATED")) it.category else
                        it.category + Category.TOP_RATED.name
                    tvSeriesDAO.insertTvSeries(
                        series.toTvSeriesEntity(
                            category = category,
                            favorite = it.addedToFavorite,
                            favoriteDate = it.addedInFavoriteDate
                        )
                    )
                } ?: tvSeriesDAO.insertTvSeries(
                    series.toTvSeriesEntity(
                        favorite = 0, favoriteDate = "", category = Category.TOP_RATED.name
                    )
                )
            }

            return result
        }
    }

    override suspend fun getTopRatedTvSeries(
        page: Int,
        isRefresh: Boolean,
        shouldCallNetwork: Boolean
    ): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())

            if(isRefresh || shouldCallNetwork){
                val result = getTopRatedTvSeriesRemote(page)
                if(result.isEmpty()){
                    emit(Resource.Error("Couldn't load data"))
                    emit(Resource.Loading(false))
                    return@flow
                } else {
                    emit(Resource.Success(result))
                    emit(Resource.Loading(false))
                    return@flow
                }
            }

            val local = tvSeriesDAO.loadCategoryTvSeries("$${Category.TOP_RATED.name}%", number = (page - 1) * 20)
            if(local.isNotEmpty()){
                emit(Resource.Success(data = local.map {
                    it.toMedia()
                }))
                emit(Resource.Loading(false))
                return@flow
            }

            val result = getTopRatedTvSeriesRemote(page)
            if(result.isEmpty()){
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            } else {
                emit(Resource.Success(result))
                emit(Resource.Loading(false))
                return@flow
            }
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
                tvSeriesDAO.insertTvSeries(dto.toTvSeriesEntity(favorite = favorite ?: 0, favoriteDate = date ?: ""))
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

    override suspend fun changeFavorite(
        favorite: Int,
        addedDate: String,
        seriesId: Int
    ): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            try {
                tvSeriesDAO.changeFavorite(favorite, addedDate, seriesId)
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