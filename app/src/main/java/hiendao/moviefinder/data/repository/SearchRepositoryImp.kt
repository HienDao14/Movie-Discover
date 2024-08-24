package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.dao.MovieDAO
import hiendao.moviefinder.data.local.dao.TvSeriesDAO
import hiendao.moviefinder.data.mapper.toMedia
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.mapper.toTvSeriesEntity
import hiendao.moviefinder.data.network.search.SearchApi
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.repository.SearchRepository
import hiendao.moviefinder.util.Category
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
                        val entity = movieDAO.getMovieWithId(searchDTO.id)
                        entity?.let {
                            val category =
                                if (it.category.contains(Category.MOVIE.name)) it.category
                                else it.category + Category.MOVIE.name
                            movieDAO.upsertMovie(
                                searchDTO.toMovieEntity(
                                    category = category,
                                    index = it.categoryIndex,
                                    favorite = it.addedToFavorite,
                                    favoriteDate = it.addedInFavoriteDate,
                                    categoryAddedDate = it.categoryDateAdded
                                )
                            )
                        } ?: movieDAO.upsertMovie(
                            searchDTO.toMovieEntity(
                                category = Category.MOVIE.name,
                                index = 10000,
                                categoryAddedDate = "empty"
                            )
                        )
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