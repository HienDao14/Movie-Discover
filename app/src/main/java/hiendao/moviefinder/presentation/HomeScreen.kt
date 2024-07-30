package hiendao.moviefinder.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.movie.TrendingMoviesSection
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.shared_components.AutoSwipeSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: MainUIState,
    navHostController: NavHostController,
    onEvent: (MainEvent) -> Unit
) {

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(MainEvent.Refresh(type = "HomeScreen"))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(refreshState.nestedScrollConnection)
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 100.dp)
                .verticalScroll(state = rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                if (uiState.isLoading) {
                    println("Loading")
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(40.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        //Popular Movies
                        AutoSwipeSection(
                            sectionType = "Movies - Top Rated",
                            movies = uiState.popularMovies,
                            navHostController = navHostController
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TrendingMoviesSection(
                            movies = uiState.trendingWeekMovies,
                            listTvSeries = emptyList(),
                            navHostController = navHostController,
                            title = "Movies - Trending Now"
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        if (refreshState.isRefreshing) {
            LaunchedEffect(true) {
                refresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                refreshState.startRefresh()
            } else {
                refreshState.endRefresh()
            }
        }

        PullToRefreshContainer(state = refreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}