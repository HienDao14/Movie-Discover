package hiendao.moviefinder.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.FavoriteRepository
import hiendao.moviefinder.presentation.state.FavoriteScreenState
import hiendao.moviefinder.presentation.uiEvent.FavoriteScreenEvent
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
): ViewModel() {

    private val _favoriteState = MutableStateFlow(FavoriteScreenState())
    val favoriteState = _favoriteState.asStateFlow()

    fun onEvent(event: FavoriteScreenEvent){
        when(event){
            is FavoriteScreenEvent.Refresh -> {
                _favoriteState.update {
                    it.copy(
                        movieFavorite = emptyList(),
                        creditFavorite = emptyList(),
                        moviePage = 1,
                        creditPage = 1
                    )
                }
                getFavoriteMovies()
                getFavoriteCredits()
            }
            is FavoriteScreenEvent.OnPaginate -> {
                if (event.type == "Movie"){
                    _favoriteState.update {
                        it.copy(
                            moviePage = it.moviePage + 1
                        )
                    }

                    getFavoriteMovies()

                } else if(event.type == "Credit"){
                    _favoriteState.update {
                        it.copy(
                            creditPage = it.creditPage + 1
                        )
                    }

                    getFavoriteCredits()
                }
            }
        }
    }


    private fun getFavoriteMovies(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val page = _favoriteState.value.moviePage
                favoriteRepository.getMovieFavorite(page = page).collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _favoriteState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Success -> {
                            result.data?.let {
                                _favoriteState.update {
                                    it.copy(
                                        movieFavorite = it.movieFavorite + result.data
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            _favoriteState.update {
                                it.copy(
                                    errorMsg = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getFavoriteCredits(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val page = _favoriteState.value.creditPage
                favoriteRepository.getCreditFavorite(page = page).collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _favoriteState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Success -> {
                            result.data?.let {
                                _favoriteState.update {
                                    it.copy(
                                        creditFavorite = it.creditFavorite + result.data
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            _favoriteState.update {
                                it.copy(
                                    errorMsg = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}