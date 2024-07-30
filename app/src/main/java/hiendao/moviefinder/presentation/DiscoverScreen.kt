package hiendao.moviefinder.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.presentation.discover.ChooseTypeDiscover
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.shared_components.CustomImage

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

    LaunchedEffect(true) {
        onEvent(
            MainEvent.StartDiscover(
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
                    println("Call api info: ${catalogQuery.value} and ${genresQuery.intValue}")
                    onEvent(
                        MainEvent.StartDiscover(
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
                Text(text = textForType.value, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
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
                Text(text = textForGenres.value, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(50.dp)
                )
            }
        }

        val listDiscoverMovie = uiState.discoverMovie

        if (listDiscoverMovie.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listDiscoverMovie.size) { index ->
                    val movie = listDiscoverMovie[index]
                    CustomImage(imageUrl = movie.posterPath, width = 100.dp, height = 180.dp) {
                        navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movie.id}")
                    }

                    if (index >= listDiscoverMovie.size - 1 && !uiState.isLoading) {
                        onEvent(MainEvent.OnPaginate("Discover"))
                    }
                }
            }
        } else {
            Text(text = "Some error occur")
        }
    }
}
