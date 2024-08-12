package hiendao.moviefinder.presentation.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.FilterType
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val prefs: SharedPreferences,
    private val tvSeriesRepository: TvSeriesRepository
) : ViewModel() {

    private val _mainUIState = MutableStateFlow(MainUIState())
    val mainUIState = _mainUIState.asStateFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.Refresh -> {
                restartState()
                if(event.type == "MoviePagedScreen"){
                    getTrendingWeekMovies(isRefresh = true)
                    getTrendingDayMovies(isRefresh = true)
                } else if(event.type == "TvSeriesPopular") {
                    getPopularTvSeries(isRefresh = true)
                } else if(event.type == "TvSeriesTopRated"){
                    getTopRatedTvSeries(isRefresh = true)
                } else {
                    load(isRefresh = true)
                }
            }

            is MainEvent.OnPaginate -> {
                _mainUIState.update {
                    it.copy(
                        moviePage = it.moviePage + 1
                    )
                }

                when(event.type){
                    FilterType.TRENDING_DAY.name -> {
                        getTrendingDayMovies(false)
                    }
                    FilterType.TRENDING_WEEK.name -> {
                        getTrendingWeekMovies(false)
                    }
                    "Discover" -> {
                        discoverMovies()
                    }
                    "DiscoverTvSeries" -> {
                        discoverTvSeries()
                    }
                    "TvSeriesPopular" -> {
                        getPopularTvSeries()
                    }
                    "TvSeriesTopRated" -> {
                        getTopRatedTvSeries()
                    }
                }
            }

            is MainEvent.StartLoad -> {
                _mainUIState.update {
                    it.copy(
                        moviePage = 0
                    )
                }
                if (event.type == FilterType.TRENDING_DAY.name) {
                    getTrendingDayMovies(false)
                } else {
                    getTrendingWeekMovies(false)
                }
            }

            is MainEvent.StartDiscover -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        _mainUIState.update {
                            it.copy(
                                moviePage = 1,
                                discoverMedia = emptyList(),
                                withGenres = event.withGenres,
                                sortBy = event.sortBy,
                                voteCountGte = event.voteCount
                            )
                        }
                        if(event.type == "Movie"){
                            discoverMovies()
                        } else {
                            discoverTvSeries()
                        }
                    }
                }
            }
        }
    }

    private fun restartState() {
        _mainUIState.update {
            it.copy(
                isLoading = false,
                isRefresh = false,
                popularMovies = emptyList(),
                trendingDayMovies = emptyList(),
                trendingWeekMovies = emptyList(),
                topRatedMovies = emptyList(),
                moviePage = 1,
                popularTvSeries = emptyList(),
                callApiInNewDate = false,
                didCheckDate = false
            )
        }
    }

    private fun load(isRefresh: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                getTopRatedMovies(isRefresh)
                getTrendingWeekMovies(isRefresh)
                getPopularTvSeries()
                getTopRatedTvSeries(isRefresh)
                if(_mainUIState.value.callApiInNewDate){
                    _mainUIState.update {
                        it.copy(
                            callApiInNewDate = false
                        )
                    }
                }
            }
        }
    }

    init {
        val date = LocalDate.now().dayOfYear
        if (date == 1) {
            _mainUIState.update {
                it.copy(
                    callApiInNewDate = true
                )
            }
            prefs.edit().putInt("Date", date).apply()
        } else {
            try {
                val dateBefore = prefs.getInt("Date", -1)
                println("Date : $date and $dateBefore")
                if (dateBefore == -1 || date > dateBefore) {
                    _mainUIState.update {
                        it.copy(
                            callApiInNewDate = true
                        )
                    }
                    prefs.edit().putInt("Date", date).apply()
                }
            } catch (e: Exception) {
                prefs.edit().putInt("Date", date).apply()
            }
        }
        load()
    }

    private fun getTopRatedMovies(isRefresh: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val page = _mainUIState.value.moviePage
                val shouldCallNetwork = _mainUIState.value.callApiInNewDate
                movieRepository.getTopRatedMovies(page, isRefresh, shouldCallNetwork)
                    .collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _mainUIState.update {
                                    it.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _mainUIState.update {
                                    it.copy(
                                        errorMsg = result.message
                                    )
                                }
                            }

                            is Resource.Success -> {
                                result.data?.let { movies ->
                                    _mainUIState.update {
                                        it.copy(
                                            popularMovies = movies.sortedByDescending { it.popularity }
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun getTrendingDayMovies(isRefresh: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val page = _mainUIState.value.moviePage
                val shouldCallNetwork = _mainUIState.value.callApiInNewDate
                movieRepository.getTrendingDayMovies(page = page, isRefresh = isRefresh)
                    .collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                _mainUIState.update {
                                    it.copy(
                                        isLoading = result.isLoading
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _mainUIState.update {
                                    it.copy(
                                        errorMsg = result.message
                                    )
                                }
                            }

                            is Resource.Success -> {
                                result.data?.let { movies ->
                                    _mainUIState.update {
                                        it.copy(
                                            trendingDayMovies = it.trendingDayMovies + movies.sortedByDescending { it.popularity }
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun getTrendingWeekMovies(isRefresh: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val page = _mainUIState.value.moviePage
                val shouldCallNetwork = _mainUIState.value.callApiInNewDate
                movieRepository.getTrendingWeekMovies(
                    page = page,
                    isRefresh = isRefresh,
                    shouldCallNetwork = shouldCallNetwork
                ).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }

                        is Resource.Error -> {
                            _mainUIState.update {
                                it.copy(
                                    errorMsg = result.message
                                )
                            }
                        }

                        is Resource.Success -> {
                            result.data?.let { movies ->
                                _mainUIState.update {
                                    it.copy(
                                        trendingWeekMovies = it.trendingWeekMovies + movies.sortedByDescending { it.popularity }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Discover
    private fun discoverMovies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val page = _mainUIState.value.moviePage
                val dateNow = LocalDate.now()
                val withGenres = _mainUIState.value.withGenres
                val sortBy = _mainUIState.value.sortBy
                val voteCountGte = _mainUIState.value.voteCountGte

                movieRepository.discoverMovies(
                    releaseDateLte = "${dateNow.year}-${dateNow.month}-${dateNow.dayOfMonth}",
                    page = page,
                    withGenres = withGenres,
                    sortBy = sortBy,
                    voteCountGte = voteCountGte
                ).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }

                        is Resource.Error -> {
                            _mainUIState.update {
                                it.copy(
                                    errorMsg = result.message
                                )
                            }
                        }

                        is Resource.Success -> {
                            result.data?.let { movies ->
                                _mainUIState.update {
                                    it.copy(
                                        discoverMedia = it.discoverMedia + movies
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun discoverTvSeries(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val page = _mainUIState.value.moviePage
                val dateNow = LocalDate.now()
                val withGenres = _mainUIState.value.withGenres
                val sortBy = _mainUIState.value.sortBy
                val voteCountGte = _mainUIState.value.voteCountGte

                tvSeriesRepository.discoverTvSeries(
                    firstAirDateLte = "${dateNow.year}-${dateNow.month}-${dateNow.dayOfMonth}",
                    page = page,
                    withGenres = withGenres,
                    sortBy = sortBy,
                    voteCountGte = voteCountGte
                ).collect{result ->
                    when (result) {
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }

                        is Resource.Error -> {
                            _mainUIState.update {
                                it.copy(
                                    errorMsg = result.message
                                )
                            }
                        }

                        is Resource.Success -> {
                            result.data?.let { listTvSeries ->
                                _mainUIState.update {
                                    it.copy(
                                        discoverMedia = it.discoverMedia + listTvSeries
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //TvSeries
    private fun getPopularTvSeries(isRefresh: Boolean = false){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val page = _mainUIState.value.moviePage
                tvSeriesRepository.getPopularTvSeries(page = page, isRefresh = isRefresh).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {list ->
                                _mainUIState.update {
                                    it.copy(
                                        popularTvSeries = it.popularTvSeries + list
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _mainUIState.update {
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

    private fun getTopRatedTvSeries(isRefresh: Boolean = false){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val page = _mainUIState.value.moviePage
                tvSeriesRepository.getTopRatedTvSeries(page = page, isRefresh = isRefresh).collect{result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {list ->
                                _mainUIState.update {
                                    it.copy(
                                        topRatedTvSeries = it.topRatedTvSeries + list
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            _mainUIState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Error -> {
                            _mainUIState.update {
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