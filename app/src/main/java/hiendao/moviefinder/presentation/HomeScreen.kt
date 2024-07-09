package hiendao.moviefinder.presentation

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.movie.TrendingMoviesSection
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.shared_components.AutoSwipeSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: MainUIState,
    navHostController: NavHostController
) {
    var searchText by remember {
        mutableStateOf("")
    }

    var searchBarActive by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return super.onPreScroll(available, source)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(connection)
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
                            sectionType = "Movies - Special",
                            movies = uiState.popularMovies,
                            navHostController = navHostController
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TrendingMoviesSection(
                            movies = uiState.trendingWeekMovies,
                           navHostController = navHostController,
                            title = "Movies - Trending Now"
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        SearchBar(
            query = searchText,
            onQueryChange = {
                searchText = it
            },
            onSearch = {
                searchBarActive = false
            },
            active = searchBarActive,
            onActiveChange = {
                searchBarActive = it
            },
            placeholder = {
                Text(text = "Search a movie or tv series")
            },
            leadingIcon = {
                if (searchBarActive) {
                    IconButton(onClick = { searchBarActive = false }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                } else
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchBarActive) {
                    IconButton(onClick = {
                        if (searchText.isNotEmpty()) {
                            searchText = ""
                        } else {
                            searchBarActive = false
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                } else
                    IconButton(onClick = {
                        Toast.makeText(context, "Voice Search Clicked", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardVoice,
                            contentDescription = "Voice Search"
                        )
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Test search screen",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}