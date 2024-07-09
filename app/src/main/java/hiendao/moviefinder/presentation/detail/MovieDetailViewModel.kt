package hiendao.moviefinder.presentation.detail

import androidx.compose.runtime.traceEventEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.presentation.state.MovieDetailState
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel() {

    private val _movieDetailState = MutableStateFlow(MovieDetailState())
    val movieDetailState = _movieDetailState.asStateFlow()



    fun load(movieId: Int){
        getMovieDetailInfo(movieId){

            getSimilarVideos(movieId)
            getCollectionVideos(_movieDetailState.value.collectionId)
        }
    }

    private fun getMovieDetailInfo(movieId: Int, onFinished: () -> Unit){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.getMovieDetail(movieId).collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _movieDetailState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _movieDetailState.update {
                                it.copy(
                                    errorMsg = result.message.toString()
                                )
                            }
                        }
                        is Resource.Success -> {
                            result.data?.let {movie ->
                                _movieDetailState.update {
                                    it.copy(
                                        movie = movie,
                                        listSimilar = movie.similar
                                    )
                                }
                            }
                        }
                    }
                }
                onFinished()
            }
        }
    }

    private fun getSimilarVideos(
        movieId: Int
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.getSimilarMovies(movieId).collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _movieDetailState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _movieDetailState.update {
                                it.copy(
                                    errorMsg = result.message.toString()
                                )
                            }
                        }
                        is Resource.Success -> {
                            result.data?.let {similar ->
                                _movieDetailState.update {
                                    it.copy(
                                        similarVideos = similar
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCollectionVideos(
        collectionId: Int?
    ){
        collectionId?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    movieRepository.getCollection(collectionId).collect{result ->
                        when(result){
                            is Resource.Loading -> {
                                _movieDetailState.update {
                                    it.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                            }
                            is Resource.Error -> {
                                _movieDetailState.update {
                                    it.copy(
                                        errorMsg = result.message.toString()
                                    )
                                }
                            }
                            is Resource.Success -> {
                                result.data?.let {collection ->
                                    _movieDetailState.update {
                                        it.copy(
                                            collectionVideos = collection
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
}