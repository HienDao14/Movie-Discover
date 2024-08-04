package hiendao.moviefinder.presentation

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.data.mapper.toMovie
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.FilterType
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _mainUIState = MutableStateFlow(MainUIState())
    val mainUIState = _mainUIState.asStateFlow()

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.Refresh -> {
                restartState()
                if(event.type == "PagedScreen"){
                    getTrendingWeekMovies(isRefresh = true)
                    getTrendingDayMovies(isRefresh = true)
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
                if (event.type == FilterType.TRENDING_DAY.name) {
                    getTrendingDayMovies(false)
                } else if (event.type == FilterType.TRENDING_WEEK.name) {
                    getTrendingWeekMovies(false)
                } else if(event.type == "Discover") {
                    discoverMovies()
                }
            }

            is MainEvent.StartLoad -> {

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
                                discoverMovie = emptyList(),
                                withGenres = event.withGenres,
                                sortBy = event.sortBy,
                                voteCountGte = event.voteCount
                            )
                        }
                        discoverMovies()
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
                trendingDayTvSeries = emptyList(),
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
                                        discoverMovie = it.discoverMovie + movies
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