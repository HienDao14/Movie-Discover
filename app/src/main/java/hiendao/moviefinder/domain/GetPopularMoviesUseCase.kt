package hiendao.moviefinder.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
){

//    fun call(): Flow<PagingData<Movie>> = Pager(
//        PagingConfig(
//            pageSize = 10,
//            prefetchDistance = 20
//        )
//    ){
//        movieRepository.getPopularMovies()
//    }
//        .flow
//        .map {value: PagingData<MovieEntity> ->
//            value.map {movieEntity : MovieEntity->
//                movieEntity.toMovie()
//            }
//        }

}