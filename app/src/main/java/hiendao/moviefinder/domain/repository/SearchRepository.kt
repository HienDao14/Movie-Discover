package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.SearchModel
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    suspend fun getSearchResponse(query: String, page: Int = 1) : Flow<Resource<List<Media>>>
}