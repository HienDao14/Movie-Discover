package hiendao.moviefinder.data.repository

import hiendao.moviefinder.data.local.CreditDAO
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.mapper.toCredit
import hiendao.moviefinder.data.mapper.toCreditEntity
import hiendao.moviefinder.data.mapper.toListCredit
import hiendao.moviefinder.data.mapper.toMovieEntity
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.domain.model.Credit
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.util.Category
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommonRepositoryImp @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDAO: MovieDAO,
    private val creditDAO: CreditDAO
) : CommonRepository {
    override suspend fun getCredits(movieId: Int): Flow<Resource<List<Credit>>> {
        return flow {
            emit(Resource.Loading())

            val movie = movieDAO.getMovieWithId(movieId)
            val credits = movie?.credits
            val listCreditId = mutableListOf<Int>()
            val listCredit = mutableListOf<Credit>()
            if (credits.isNullOrEmpty()) {
                val remoteCredits = movieApi.getCredits(movieId)
                if (!remoteCredits.cast.isNullOrEmpty()) {
                    remoteCredits.cast.sortedByDescending { it.popularity }
                        .filter { it.character != null }.take(20).forEach { cast ->
                        val localCredit = creditDAO.getCredit(cast.id)
                        if (localCredit != null) {
                            if(!localCredit.movieId.contains(movieId.toString())){
                                val movieIds = localCredit.movieId + ",${movieId}"
                                val characters = localCredit.character + ",${cast.character}"
                                creditDAO.insertCredit(characters, movieIds, cast.id)
                            }
                        } else {
                            val creditEntity = cast.toCreditEntity(movieId)

                            creditDAO.upsertCredit(creditEntity)
                            listCredit.add(creditEntity.toCredit())
                            listCreditId.add(cast.id)
                        }
                    }
                }

                if (!remoteCredits.crew.isNullOrEmpty()) {
                    remoteCredits.crew.filter {
                        it.job != null && it.job == "Director"
                    }.forEach { crew ->
                        val creditEntity = crew.toCreditEntity(movieId)
                        creditDAO.upsertCredit(creditEntity)
                        listCredit.add(creditEntity.toCredit())
                        listCreditId.add(crew.id)
                    }
                }

                movieDAO.updateCredits(
                    credit = listCreditId.joinToString(",") { it.toString() },
                    movieId = movieId
                )
            }

            val localCredits = creditDAO.getCredits("%${movieId}%")
            emit(Resource.Success(data = localCredits.toListCredit()))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun getCreditDetail(personId: Int): Flow<Resource<Credit>> {
        return flow {
            emit(Resource.Loading())

            val localCredit = creditDAO.getCredit(personId)
            if (localCredit == null || (localCredit.birthday == "" && localCredit.placeOfBirth == "")) {
                val remoteCredit = movieApi.getCreditDetail(personId = personId)
                val type = if (remoteCredit.known_for_department == "Directing") "Crew" else "Cast"
                remoteCredit.movie_credits?.let {
                    if (type == "Cast") {
                        it.cast?.filter { it?.character != null }?.forEach {
                            it?.let { movie ->
                                movieDAO.insertMovie(movie.toMovieEntity(Category.MOVIE, 1))
                            }
                        }
                    } else {
                        it.crew?.filter { it?.job == "Director" }?.forEach {
                            it?.let { movie ->
                                movieDAO.insertMovie(movie.toMovieEntity(Category.MOVIE, 1))
                            }
                        }
                    }
                }
                creditDAO.upsertCredit(remoteCredit.toCreditEntity(type))
                emit(Resource.Success(data = remoteCredit.toCredit(type)))
                emit(Resource.Loading(false))
                return@flow
            }

            emit(Resource.Success(data = localCredit.toCredit()))
            emit(Resource.Loading(false))
            return@flow
        }
    }

    override suspend fun changeFavoriteCredit(
        favorite: Int,
        addedDate: String,
        personId: Int
    ): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            try {
                creditDAO.changeFavoriteCredit(favorite = favorite, addedDate = addedDate, creditId = personId)
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