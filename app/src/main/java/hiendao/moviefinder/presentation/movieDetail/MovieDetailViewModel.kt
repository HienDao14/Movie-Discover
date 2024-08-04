package hiendao.moviefinder.presentation.movieDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.presentation.state.FavoriteState
import hiendao.moviefinder.presentation.state.MovieDetailState
import hiendao.moviefinder.presentation.uiEvent.MovieDetailEvent
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val commonRepository: CommonRepository
): ViewModel() {

    private val _movieDetailState = MutableStateFlow(MovieDetailState())
    val movieDetailState = _movieDetailState.asStateFlow()

    private val _favoriteState = MutableSharedFlow<FavoriteState>()
    val favoriteState = _favoriteState.asSharedFlow()

    fun onEvent(event: MovieDetailEvent){
        when(event){
            is MovieDetailEvent.Refresh -> {
                reload()
                _movieDetailState.update {
                    it.copy(
                        isLoading = true
                    )
                }
                load(event.movieId)
            }
            is MovieDetailEvent.onPaginate -> {

            }

            is MovieDetailEvent.AddToFavorite -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        _favoriteState.emit(
                            FavoriteState(
                                isLoading = false,
                                isSuccess = null,
                                errorMsg = null
                            )
                        )
                        changeFavorite(event.favorite, event.date, event.movieId)
                    }
                }
            }
        }
    }

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
                collectionVideos = emptyList(),
                listCredit = emptyList()
            )
        }
    }
    fun load(movieId: Int){
        getMovieDetailInfo(movieId){
            getCollectionVideos(_movieDetailState.value.collectionId)
        }
        getSimilarVideos(movieId)
        getCredits(movieId)
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

    private fun getCredits(
        movieId: Int
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                commonRepository.getCredits(movieId).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {credits ->
                                Log.d("credit data before update", credits.joinToString(","){it.name})
                                _movieDetailState.update {
                                    it.copy(
                                        listCredit = credits
                                    )
                                }
                            }
                        }
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
                    }
                }
            }
        }
    }

    private fun changeFavorite(
        favorite: Int, addedDate: String, movieId: Int
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.changeFavoriteMovie(favorite, addedDate, movieId).collect{result ->
                    when(result){
                        is Resource.Success -> Unit
                        is Resource.Loading -> Unit
                        is Resource.Error -> {
                           _movieDetailState.update {
                               it.copy(
                                   errorMsg = result.message.toString()
                               )
                           }
                        }
                    }
                }
            }
        }
    }
}