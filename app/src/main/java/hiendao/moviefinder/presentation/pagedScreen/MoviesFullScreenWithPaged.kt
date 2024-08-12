package hiendao.moviefinder.presentation.pagedScreen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hiendao.moviefinder.data.mapper.toListMedia
import hiendao.moviefinder.presentation.pagedScreen.common.MediaItemColumnScreen
import hiendao.moviefinder.presentation.pagedScreen.common.MediaItemGridScreen
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.FilterType
import hiendao.moviefinder.util.ListingType
import hiendao.moviefinder.util.NavRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesFullScreenWithPaged(
    modifier: Modifier = Modifier,
    type: String,
    navHostController: NavHostController,
    uiState: MainUIState,
    onEvent: (MainEvent) -> Unit
) {

    val listingType = rememberSaveable {
        mutableStateOf(ListingType.GRID)
    }
    val filter = rememberSaveable {
        mutableStateOf(FilterType.TRENDING_WEEK)
    }
    var filterShow by remember {
        mutableStateOf(false)
    }

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(MainEvent.Refresh(type = "MoviePagedScreen"))
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    val screenTitle =
        if (filter.value == FilterType.TRENDING_WEEK) "Movies - Trending Week" else "Movies - Trending Day"
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
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

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

                    Box {
                        IconButton(
                            onClick = {
                                filterShow = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterAlt,
                                contentDescription = "Filter"
                            )
                        }

                        DropdownMenu(expanded = filterShow, onDismissRequest = { filterShow = false }) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Trending Week")
                                },
                                onClick = {
                                    if (filter.value != FilterType.TRENDING_WEEK) {
                                        filter.value = FilterType.TRENDING_WEEK
                                        onEvent(MainEvent.StartLoad(filter.value.name))
                                    }
                                    filterShow = false
                                },
                                trailingIcon = {
                                    if (filter.value == FilterType.TRENDING_WEEK) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Checked"
                                        )
                                    }
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = if (filter.value == FilterType.TRENDING_WEEK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    trailingIconColor = if (filter.value == FilterType.TRENDING_WEEK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                )
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Trending Day")
                                },
                                onClick = {
                                    if (filter.value != FilterType.TRENDING_DAY) {
                                        filter.value = FilterType.TRENDING_DAY
                                        onEvent(MainEvent.StartLoad(filter.value.name))
                                    }
                                    filterShow = false
                                },
                                trailingIcon = {
                                    if (filter.value == FilterType.TRENDING_DAY) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Checked"
                                        )
                                    }
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = if (filter.value == FilterType.TRENDING_DAY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    trailingIconColor = if (filter.value == FilterType.TRENDING_DAY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = modifier.fillMaxWidth()) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp),
                            color = Color.White
                        )
                    }
                    val listMedia =
                        if (filter.value == FilterType.TRENDING_DAY) uiState.trendingDayMovies.toListMedia() else uiState.trendingWeekMovies.toListMedia()
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
                                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${media.id}")
                                        })

                                    if (index >= listMedia.size - 1 && !uiState.isLoading) {
                                        onEvent(MainEvent.OnPaginate(filter.value.name))
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
                                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${media.id}")
                                        }
                                    )

                                    if (index >= listMedia.size - 1 && !uiState.isLoading) {
                                        onEvent(MainEvent.OnPaginate(filter.value.name))
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