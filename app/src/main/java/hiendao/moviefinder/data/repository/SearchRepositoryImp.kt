package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.mapper.toSearchModel
import hiendao.moviefinder.data.network.search.SearchApi
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.model.SearchModel
import hiendao.moviefinder.domain.repository.SearchRepository
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchRepositoryImp @Inject constructor(
    private val searchApi: SearchApi,
    private val movieDAO: MovieDAO
): SearchRepository {

    override suspend fun getSearchResponse(query: String, page: Int): Flow<Resource<List<SearchModel>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = searchApi.getSearchMulti(query, page)
                val listResponse = mutableListOf<SearchModel>()

                if(response.results.isNullOrEmpty()){
                    emit(Resource.Loading(false))
                    return@flow
                }

                response.results.forEach { searchDTO ->
                    if(searchDTO.media_type == "movie"){
                        val movieEntity = searchDTO.toMovieEntity()
                        movieDAO.insertMovie(movieEntity)
                        listResponse.add(searchDTO.toSearchModel())
                    } else {
                        //"Insert to tv series database"
                    }
                }

                emit(Resource.Success(listResponse))
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