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

    fun reload(){
        _movieDetailState.update {
            it.copy(
                isLoading = false,
                errorMsg = "",
                movieId = null,
                collectionId = null,
                movie = null,
                listSimilar = emptyList(),
                similarVideos = emptyList(),
                collectionVideos = emptyList()
            )
        }
    }
    fun load(movieId: Int){
        println("Similar videos: ${_movieDetailState.value.similarVideos}")
        getMovieDetailInfo(movieId){
            getCollectionVideos(_movieDetailState.value.collectionId)
        }
        getSimilarVideos(movieId)
    }

    private fun getMovieDetailInfo(movieId: Int, onFinish: () -> Unit){
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
                                        movieId = movie.id,
                                        collectionId = movie.collectionId
                                    )
                                }
                            }
                        }
                    }
                }
                onFinish()
            }
        }
    }

    private fun getSimilarVideos(
        movieId: Int
    ){
        println("MovieId: $movieId")
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
        if(collectionId == -1){
            _movieDetailState.update {
                it.copy(
                    collectionVideos = emptyList()
                )
            }
            return
        }
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