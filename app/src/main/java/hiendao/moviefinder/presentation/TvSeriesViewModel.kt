package hiendao.moviefinder.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import javax.inject.Inject

@HiltViewModel
class TvSeriesViewModel @Inject constructor(
    private val tvSeriesRepository: TvSeriesRepository
): ViewModel() {



    private fun getPopularTvSeries(){

    }

}