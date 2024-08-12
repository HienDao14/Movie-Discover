package hiendao.moviefinder.presentation.detail.creditDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.presentation.state.CreditState
import hiendao.moviefinder.presentation.uiEvent.CreditScreenEvent
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreditViewModel @Inject constructor(
    private val commonRepository: CommonRepository,
    private val movieRepository: MovieRepository
): ViewModel() {

    private val _creditState = MutableStateFlow(CreditState())
    val creditState = _creditState.asStateFlow()

    fun onEvent(event: CreditScreenEvent){
        when(event){
            is CreditScreenEvent.ChangeFavorite -> {
                changeFavorite(event.favorite, event.addedDate, event.creditId)
            }
            is CreditScreenEvent.Refresh -> {
                onRefresh()
            }

        }
    }

    fun reload(){
        _creditState.update {
            it.copy(
                isLoading = false,
                errorMsg = null,
                movies = emptyList(),
                creditId = 0,
                creditDetail = null,
                changeFavorite = null,
                loadingFor = ""
            )
        }
    }

    fun load(creditId: Int){
        getCreditDetail(creditId){
            getMovies(creditId)
        }
    }

    private fun onRefresh(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _creditState.update {
                    it.copy(
                        isLoading = true,
                        loadingFor = "credit_detail"
                    )
                }

                load(creditState.value.creditId)
            }
        }
    }

    private fun changeFavorite(favorite: Int, addedDate: String, creditId: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _creditState.update {
                    it.copy(
                        isLoading = true,
                        loadingFor = "favorite"
                    )
                }
                commonRepository.changeFavoriteCredit(favorite, addedDate, creditId).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {success->
                                _creditState.update {
                                    it.copy(
                                        changeFavorite = success
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _creditState.update {
                                it.copy(
                                    isLoading = result.isLoading,
                                    loadingFor = "favorite"
                                )
                            }
                        }
                        is Resource.Error -> {
                            _creditState.update {
                                it.copy(
                                    changeFavorite = false,
                                    errorMsg = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCreditDetail(
        creditId: Int,
        onFinish: (Int) -> Unit
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                commonRepository.getCreditDetail(creditId).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {credit ->
                                _creditState.update {
                                    it.copy(
                                        creditDetail = credit,
                                        creditId = creditId
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _creditState.update {
                                it.copy(
                                    isLoading = result.isLoading,
                                    loadingFor = "credit_detail"
                                )
                            }
                        }
                        is Resource.Error -> {
                            _creditState.update {
                                it.copy(
                                    errorMsg = result.message
                                )
                            }
                        }
                    }
                }
                onFinish(creditId)
            }
        }
    }

    private fun getMovies(
        creditId: Int
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                movieRepository.getMoviesWithCreditId(creditId).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {movies ->
                                Log.d("movies credit", movies.joinToString(","){it.title})
                                _creditState.update {
                                    it.copy(
                                        movies = movies
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _creditState.update {
                                it.copy(
                                    isLoading = result.isLoading,
                                    loadingFor = "movies"
                                )
                            }
                        }
                        is Resource.Error -> {
                            Log.d("movies credit", result.message.toString())
                            _creditState.update {
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