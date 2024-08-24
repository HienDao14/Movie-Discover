package hiendao.moviefinder.presentation.main.discover

import android.graphics.Paint.Align
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.shared_components.CustomImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    uiState: MainUIState,
    navHostController: NavHostController,
    onEvent: (MainEvent) -> Unit
) {

    val textForType = rememberSaveable {
        mutableStateOf("Movie")
    }

    val textForCatalog = rememberSaveable {
        mutableStateOf("Popular")
    }

    val textForGenres = rememberSaveable {
        mutableStateOf("All genres")
    }

    val showDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val typeForDialog = rememberSaveable {
        mutableStateOf("")
    }

    val currentTypeIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    val currentCatalogIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    val currentGenresIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    val catalogQuery = rememberSaveable {
        mutableStateOf("popularity.desc")
    }

    val genresQuery = rememberSaveable {
        mutableIntStateOf(-1)
    }

    val refreshScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        isRefreshing = true
        //function refresh
        onEvent(
            MainEvent.StartDiscover(
                type = textForType.value,
                sortBy = catalogQuery.value,
                voteCount = 1000f,
                withGenres = if (genresQuery.intValue == -1) null else genresQuery.intValue.toString()
            )
        )
        delay(3000)
        isRefreshing = false
    }

    val refreshState = rememberPullToRefreshState()

    LaunchedEffect(true) {
        onEvent(
            MainEvent.StartDiscover(
                type = textForType.value,
                sortBy = catalogQuery.value,
                voteCount = 1000f,
                withGenres = if (genresQuery.intValue == -1) null else genresQuery.intValue.toString()
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (showDialog.value) {
            ChooseTypeDiscover(
                isMovie = textForType.value == "Movie",
                type = typeForDialog.value,
                setShowDialog = {
                    showDialog.value = it
                },
                setInfo = { item, index ->
                    when (typeForDialog.value) {

                        "Type" -> {
                            textForType.value = item.name
                            currentTypeIndex.intValue = index
                        }

                        "Catalog" -> {
                            textForCatalog.value = item.name
                            catalogQuery.value = item.apiQuery.toString()
                            currentCatalogIndex.intValue = index
                        }

                        else -> {
                            textForGenres.value = item.name
                            genresQuery.intValue = item.id ?: 0
                            currentGenresIndex.intValue = index
                        }
                    }

                    //Call api
                    onEvent(
                        MainEvent.StartDiscover(
                            type = textForType.value,
                            sortBy = catalogQuery.value,
                            voteCount = 1000f,
                            withGenres = if (genresQuery.intValue == -1) null else genresQuery.intValue.toString()
                        )
                    )
                },
                currentIndex = when (typeForDialog.value) {
                    "Type" -> currentTypeIndex.intValue
                    "Catalog" -> currentCatalogIndex.intValue
                    else -> currentGenresIndex.intValue
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(refreshState.nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp)

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier.clickable {
                        showDialog.value = true
                        typeForDialog.value = "Type"
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                    Text(
                        text = textForType.value,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier.clickable {
                        showDialog.value = true
                        typeForDialog.value = "Catalog"
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                    Text(
                        text = textForCatalog.value,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Row(
                    modifier = Modifier.clickable {
                        showDialog.value = true
                        typeForDialog.value = "Genres"
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                    Text(
                        text = textForGenres.value,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

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

            val listDiscoverMedia = uiState.discoverMedia

            if (listDiscoverMedia.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listDiscoverMedia.size, key = {
                        it
                    }) { index ->
                        val media = listDiscoverMedia[index]
                        CustomImage(imageUrl = media.posterPath, width = 100.dp, height = 180.dp) {
                            if (textForType.value == "Movie") {
                                navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${media.id}")
                            } else {
                                navHostController.navigate("${NavRoute.DETAIL_SCREEN}?seriesId=${media.id}")
                            }
                        }

                        if (index >= listDiscoverMedia.size - 1 && !uiState.isLoading) {
                            if (textForType.value == "Movie") {
                                onEvent(MainEvent.OnPaginate("Discover"))
                            } else {
                                onEvent(MainEvent.OnPaginate("DiscoverTvSeries"))
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
