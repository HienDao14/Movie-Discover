package hiendao.moviefinder.presentation.detail.tvSeriesDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.presentation.state.SeriesDetailState
import hiendao.moviefinder.presentation.uiEvent.SeriesDetailEvent
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    private val tvSeriesRepository: TvSeriesRepository,
    private val commonRepository: CommonRepository
): ViewModel() {

    private val _seriesState = MutableStateFlow(SeriesDetailState())
    val seriesState = _seriesState.asStateFlow()

    fun onEvent(event: SeriesDetailEvent){
        when(event){
            is SeriesDetailEvent.Refresh -> {
                reload()
                _seriesState.update {
                    it.copy(
                        isLoading = true
                    )
                }
                load(isRefresh = true, id = event.seriesId)
            }
            is SeriesDetailEvent.AddToFavorite -> {
                changeFavorite(event.favorite, event.date, event.seriesId)
            }
        }
    }

    fun reload(){
        _seriesState.update {
            it.copy(
                isLoading = false,
                errorMsg = null,
                series = null,
                credits = emptyList()
            )
        }
    }

    fun load(isRefresh: Boolean = false, id: Int){
        getTvSeriesDetail(isRefresh, id)
        getCredits(id)
        getSimilarMedias(id)
    }

    private fun getTvSeriesDetail(isRefresh: Boolean = false, id: Int){

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                tvSeriesRepository.getTvSeriesDetail(isRefresh, id).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {series ->
                                _seriesState.update {
                                    it.copy(
                                        series = series
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _seriesState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _seriesState.update {
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

    private fun getCredits(id: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                commonRepository.getCredits("TvSeries", id).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {credits ->
                                _seriesState.update {
                                    it.copy(
                                        credits = credits
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _seriesState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _seriesState.update {
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

    private fun getSimilarMedias(id: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                commonRepository.getSimilarMovies("TvSeries", id).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {similarMedias ->
                                _seriesState.update {
                                    it.copy(
                                        similar = similarMedias
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _seriesState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _seriesState.update {
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

    private fun changeFavorite(
        favorite: Int, addedDate: String, movieId: Int
    ){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                tvSeriesRepository.changeFavorite(favorite, addedDate, movieId).collect{result ->
                    when(result){
                        is Resource.Success -> Unit
                        is Resource.Loading -> Unit
                        is Resource.Error -> {
                            _seriesState.update {
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