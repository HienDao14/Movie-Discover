package hiendao.moviefinder.presentation.main.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.state.FavoriteScreenState
import hiendao.moviefinder.presentation.uiEvent.FavoriteScreenEvent
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.shared_components.CustomImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    favoriteScreenState: FavoriteScreenState,
    onEvent: (FavoriteScreenEvent) -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(FavoriteScreenEvent.Refresh("Favorite"))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    val movies = favoriteScreenState.movieFavorite
    val credits = favoriteScreenState.creditFavorite
    val listSeries = favoriteScreenState.seriesFavorite

    LaunchedEffect(key1 = true) {
        onEvent(FavoriteScreenEvent.Refresh("Favorite"))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(refreshState.nestedScrollConnection)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 100.dp, start = 10.dp, end = 10.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            if (movies.isNotEmpty()) {
                Text(
                    text = "Favorite Movies",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(movies.size) { index ->
                        val movie = movies[index]
                        CustomImage(imageUrl = movie.posterPath, width = 120.dp, height = 180.dp) {
                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movie.id}")
                        }
                        if (index >= movies.size - 1 && !favoriteScreenState.isLoading) {
                            onEvent(FavoriteScreenEvent.OnPaginate("Movie"))
                        }

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (listSeries.isNotEmpty()) {
                Text(
                    text = "Favorite Tv Series",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listSeries.size) { index ->
                        val series = listSeries[index]
                        CustomImage(imageUrl = series.posterPath, width = 120.dp, height = 180.dp) {
                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?seriesId=${series.id}")
                        }
                        if (index >= movies.size - 1 && !favoriteScreenState.isLoading) {
                            onEvent(FavoriteScreenEvent.OnPaginate("Tv Series"))
                        }

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (credits.isNotEmpty()) {
                Text(
                    text = "Favorite Credits",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(credits.size) { index ->
                        val credit = credits[index]
                        CustomImage(
                            imageUrl = credit.profilePath,
                            width = 120.dp,
                            height = 180.dp
                        ) {
                            navHostController.navigate("${NavRoute.CREDIT_SCREEN}?creditId=${credit.id}")
                        }
                        if (index >= credits.size - 1 && !favoriteScreenState.isLoading) {
                            onEvent(FavoriteScreenEvent.OnPaginate("Credit"))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
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