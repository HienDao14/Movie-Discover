package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.dao.MovieDAO
import hiendao.moviefinder.data.local.dao.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toMedia
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.mapper.toTvSeriesEntity
import hiendao.moviefinder.data.network.search.SearchApi
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.repository.SearchRepository
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchRepositoryImp @Inject constructor(
    private val searchApi: SearchApi,
    private val movieDAO: MovieDAO,
    private val tvSeriesDAO: TvSeriesDAO
): SearchRepository {

    override suspend fun getSearchResponse(query: String, page: Int): Flow<Resource<List<Media>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = searchApi.getSearchMulti(query, page)

                if(response.results.isNullOrEmpty()){
                    emit(Resource.Loading(false))
                    return@flow
                }

                response.results.forEach { searchDTO ->
                    if(searchDTO.media_type == "movie"){
                        val movieEntity = searchDTO.toMovieEntity()
                        movieDAO.insertMovie(movieEntity)
                    } else {
                        //"Insert to tv series database"
                        val series = searchDTO.toTvSeriesEntity()
                        tvSeriesDAO.insertTvSeries(series)
                    }
                }

                val result = response.results.map {
                    it.toMedia()
                }

                emit(Resource.Success(result))
                emit(Resource.Loading(false))
                return@flow
            } catch (e : Exception){
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
                return@flow
            }
        }
    }
}