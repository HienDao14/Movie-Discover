package hiendao.moviefinder.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel(){

    private val _mainUIState = MutableStateFlow(MainUIState())
    val mainUIState = _mainUIState.asStateFlow()

    val popularMoviesPagedData : Flow<PagingData<Movie>> = movieRepository.getPopularMoviesPaged()
        .map {pagingData ->
            pagingData.map {
                it.toMovie()
            }
        }
        .cachedIn(viewModelScope)

    init {
        getPopularMovies()
        getTrendingWeekMovies()
    }

    private fun getPopularMovies(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.getPopularMovies().collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Success -> {
                            result.data?.let {movies ->
                                _mainUIState.update {
                                    it.copy(
                                        popularMovies = movies
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getTrendingDayMovies(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.getTrendingDayMovies().collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Success -> {
                            result.data?.let {movies ->
                                _mainUIState.update {
                                    it.copy(
                                        trendingDayMovies = movies
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getTrendingWeekMovies(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.getTrendingWeekMovies().collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Success -> {
                            result.data?.let {movies ->
                                _mainUIState.update {
                                    it.copy(
                                        trendingWeekMovies = movies
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}