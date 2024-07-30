package hiendao.moviefinder.presentation


import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.model.BottomBarItem
import hiendao.moviefinder.presentation.search.SearchScreen
import hiendao.moviefinder.presentation.search.SearchViewModel
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.presentation.uiEvent.SearchEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainUIState: MainUIState,
    navHostController: NavHostController,
    onEvent: (MainEvent) -> Unit
) {
    val screenSelected = rememberSaveable {
        mutableIntStateOf(0)
    }

    val bottomItems = listOf(
        BottomBarItem(
            title = "Home",
            iconSelected = Icons.Filled.Home,
            iconUnSelected = Icons.Outlined.Home
        ),
        BottomBarItem(
            title = "Discover",
            iconSelected = Icons.Filled.Explore,
            iconUnSelected = Icons.Outlined.Explore
        ),
        BottomBarItem(
            title = "Favorite",
            iconSelected = Icons.Filled.Favorite,
            iconUnSelected = Icons.Outlined.FavoriteBorder
        )
    )

    //Search
    var searchText by remember {
        mutableStateOf("")
    }

    var searchBarActive by remember {
        mutableStateOf(false)
    }
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                bottomItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == screenSelected.intValue,
                        onClick = { screenSelected.intValue = index },
                        icon = {
                            if (index == screenSelected.intValue) {
                                Icon(
                                    imageVector = item.iconSelected,
                                    contentDescription = item.title
                                )
                            } else
                                Icon(
                                    imageVector = item.iconUnSelected,
                                    contentDescription = item.title
                                )
                        },
                        label = {
                            Text(text = item.title)
                        }
                    )
                }
            }
        }
    ) { paddingValue ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = paddingValue.calculateBottomPadding())
        ) {
            when (screenSelected.intValue) {
                0 -> HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    uiState = mainUIState,
                    navHostController = navHostController,
                    onEvent
                )

                1 -> DiscoverScreen(
                    modifier = Modifier.fillMaxSize(),
                    uiState = mainUIState,
                    navHostController = navHostController,
                    onEvent = onEvent
                )

                2 -> HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    uiState = mainUIState,
                    navHostController = navHostController,
                    onEvent
                )
            }


            LaunchedEffect(searchBarActive) {
                if(!searchBarActive){
                    println("Search bar close")
                    searchText = ""
                    searchViewModel.onEvent(SearchEvent.OnExit("Search Screen"))
                }
            }

            SearchBar(
                query = searchText,
                onQueryChange = {
                    searchText = it
                    if(it.isNotEmpty()){
                        searchViewModel.onEvent(SearchEvent.OnQueryChange(it))
                    } else {
                        searchViewModel.onEvent(SearchEvent.OnExit("Search Screen"))
                    }
                },
                onSearch = {
                    searchViewModel.onEvent(SearchEvent.OnQueryChange(it))
                },
                active = searchBarActive,
                onActiveChange = {
                    searchBarActive = it
                },
                placeholder = {
                    Text(text = "Search a movie or tv series", fontSize = 15.sp)
                },
                leadingIcon = {
                    if (searchBarActive) {
                        IconButton(onClick = {
                            searchBarActive = false
//                        searchText = ""
                        }) {
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
                                searchViewModel.onEvent(SearchEvent.OnExit("Search Screen"))
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
                SearchScreen(
                    modifier = modifier.fillMaxSize(),
                    searchState = searchViewModel.searchState.collectAsState().value,
                    onEvent = searchViewModel::onEvent,
                    navHostController = navHostController
                )
            }
        }
    }
}