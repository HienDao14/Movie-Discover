package hiendao.moviefinder.presentation.tvSeriesDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.presentation.state.SeriesDetailState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    private val tvSeriesRepository: TvSeriesRepository
): ViewModel() {

    private val _seriesState = MutableStateFlow(SeriesDetailState())
    val seriesState = _seriesState.asStateFlow()

    private fun getTvSeriesDetail(){

        viewModelScope.launch {
            withContext(Dispatchers.IO){



            }
        }

    }

}