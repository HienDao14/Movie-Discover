package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommonRepository {

    suspend fun getCredits(type: String, id: Int): Flow<Resource<List<Credit>>>

    suspend fun getCreditDetail(personId: Int): Flow<Resource<Credit>>

    suspend fun changeFavoriteCredit(favorite: Int, addedDate: String, personId: Int): Flow<Resource<Boolean>>

    suspend fun getSimilarMovies(type: String = "Movie", id: Int): Flow<Resource<List<Media>>>
}