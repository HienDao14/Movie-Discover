package hiendao.moviefinder.presentation.pagedScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.pagedScreen.common.MediaItemColumnScreen
import hiendao.moviefinder.presentation.pagedScreen.common.MediaItemGridScreen
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.ListingType
import hiendao.moviefinder.util.NavRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvSeriesFullScreenWithPaged(
    modifier: Modifier = Modifier,
    type: String,
    navHostController: NavHostController,
    uiState: MainUIState,
    onEvent: (MainEvent) -> Unit
) {

    val listingType = rememberSaveable {
        mutableStateOf(ListingType.GRID)
    }

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(MainEvent.Refresh(type = type))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    val screenTitle = if(type == "TvSeriesPopular") "Tv Series - Popular" else "Tv Series - Top Rated"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = screenTitle)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .nestedScroll(refreshState.nestedScrollConnection)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(height = 50.dp, width = 200.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Loading...")
                        }
                    }
                }

                if (!uiState.errorMsg.isNullOrEmpty()) {
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(height = 50.dp, width = 200.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Some error occur!!! Pull to refresh", fontSize = 13.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        listingType.value = ListingType.GRID

                    }) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Grid view",
                            tint = if (listingType.value == ListingType.GRID) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = {
                        listingType.value = ListingType.COLUMN
                    }) {
                        Icon(
                            imageVector = Icons.Default.TableRows,
                            contentDescription = "Column view",
                            tint = if (listingType.value == ListingType.COLUMN) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = modifier.fillMaxWidth()) {

                    val listMedia =
                        if (type == "TvSeriesPopular") uiState.popularTvSeries else uiState.topRatedTvSeries
                    if (listMedia.isNotEmpty()) {
                        if (listingType.value == ListingType.GRID) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(listMedia.size) { index ->
                                    val media = listMedia[index]
                                    MediaItemGridScreen(
                                        media = media,
                                        navigate = {
                                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?seriesId=${media.id}")
                                        })

                                    if (index >= listMedia.size - 1 && !uiState.isLoading) {
                                        onEvent(MainEvent.OnPaginate(type = type))
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(listMedia.size) { index ->
                                    val media = listMedia[index]
                                    MediaItemColumnScreen(
                                        media = media,
                                        navigate = {
                                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?seriesId=${media.id}")
                                        }
                                    )

                                    if (index >= listMedia.size - 1 && !uiState.isLoading) {
                                        onEvent(MainEvent.OnPaginate(type = type))
                                    }
                                }
                            }
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
}