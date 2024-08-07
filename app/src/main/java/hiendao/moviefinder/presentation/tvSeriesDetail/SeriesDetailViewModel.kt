package hiendao.moviefinder.presentation.tvSeriesDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.presentation.state.SeriesDetailState
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
}