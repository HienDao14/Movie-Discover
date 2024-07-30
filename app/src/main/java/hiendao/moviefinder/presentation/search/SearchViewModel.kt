package hiendao.moviefinder.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hiendao.moviefinder.domain.repository.SearchRepository
import hiendao.moviefinder.presentation.state.SearchState
import hiendao.moviefinder.presentation.uiEvent.SearchEvent
import hiendao.moviefinder.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
): ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    fun onEvent(event: SearchEvent){
        when(event){
            is SearchEvent.Refresh -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        _searchState.update {
                            it.copy(
                                searchPage = 1,
                                searchQuery = event.query
                            )
                        }

                        getSearchResponse()
                    }
                }
            }
            is SearchEvent.OnPaginate -> {
                _searchState.update {
                    it.copy(
                        searchPage = it.searchPage + 1
                    )
                }
                getSearchResponse()
            }
            is SearchEvent.OnQueryChange -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){

                        delay(500L)
                        _searchState.update {
                            it.copy(
                                searchResponse = emptyList(),
                                searchQuery = event.query,
                                searchPage = 1
                            )
                        }
                        if(event.query.isNotEmpty()){
                            getSearchResponse()
                        }
                    }
                }
            }
            is SearchEvent.OnExit -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        _searchState.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = null,
                                searchQuery = "",
                                searchPage = 1,
                                searchResponse = emptyList()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun reInitState(){
        _searchState.update {
            it.copy(
                isLoading = false,
                errorMsg = null,
                searchQuery = "",
                searchPage = 1,
                searchResponse = emptyList()
            )
        }
    }

    private fun getSearchResponse(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val query = _searchState.value.searchQuery
                val page = _searchState.value.searchPage
                searchRepository.getSearchResponse(query, page).collect{result ->
                    when(result){
                        is Resource.Loading -> {
                            _searchState.update {
                                it.copy(
                                    isLoading = result.isLoading
                                )
                            }
                        }
                        is Resource.Success -> {
                            result.data?.let {searchList ->
                                _searchState.update {
                                    it.copy(
                                        searchResponse = it.searchResponse + searchList
                                    )
                                }
                            }
                        }
                        is Resource.Error -> {
                            _searchState.update {
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