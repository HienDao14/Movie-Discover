package hiendao.moviefinder.domain.repository

import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow

interface CommonRepository {

    suspend fun getCredits(movieId: Int): Flow<Resource<List<Credit>>>

    suspend fun getCreditDetail(personId: Int): Flow<Resource<Credit>>

    suspend fun changeFavoriteCredit(favorite: Int, addedDate: String, personId: Int): Flow<Resource<Boolean>>
}