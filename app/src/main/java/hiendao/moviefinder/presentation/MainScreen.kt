package hiendao.moviefinder.presentation


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import hiendao.moviefinder.presentation.model.BottomBarItem
import hiendao.moviefinder.presentation.search.SearchScreen
import hiendao.moviefinder.presentation.search.SearchViewModel
import hiendao.moviefinder.presentation.state.FavoriteScreenState
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.presentation.uiEvent.FavoriteScreenEvent
import hiendao.moviefinder.presentation.uiEvent.MainEvent
import hiendao.moviefinder.presentation.uiEvent.SearchEvent
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainUIState: MainUIState,
    favoriteScreenState: FavoriteScreenState,
    navHostController: NavHostController,
    onEvent: (MainEvent) -> Unit,
    onFavoriteEvent: (FavoriteScreenEvent) -> Unit
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


    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    var canRecord by remember {
        mutableStateOf(hasPermission)
    }

    val recordLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val result: String? =
                    it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        .let { results ->
                            results?.get(0)
                        }
                result?.let {
                    searchBarActive = true
                    searchText = it
                    searchViewModel.onEvent(SearchEvent.OnQueryChange(it))
                }
            }
        }
    val recordAudioLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            canRecord = isGranted
        }

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

                2 -> FavoriteScreen(
                    modifier = Modifier.fillMaxSize(),
                    navHostController = navHostController,
                    favoriteScreenState = favoriteScreenState,
                    onEvent = onFavoriteEvent
                )
            }


            LaunchedEffect(searchBarActive) {
                if (!searchBarActive) {
                    println("Search bar close")
                    searchText = ""
                    searchViewModel.onEvent(SearchEvent.OnExit("Search Screen"))
                }
            }

            SearchBar(
                query = searchText,
                onQueryChange = {
                    searchText = it
                    if (it.isNotEmpty()) {
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
                    Row {
                        IconButton(onClick = {
                            if (!canRecord) {
                                recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                val intent =
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE,
                                            "en"
                                        )
                                        putExtra(
                                            RecognizerIntent.EXTRA_PROMPT,
                                            "Talk your searching movie title"
                                        )


                                    }
                                recordLauncher.launch(intent)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardVoice,
                                contentDescription = "Voice Search"
                            )
                        }
                        if (searchBarActive) {
                            Spacer(modifier = Modifier.width(5.dp))
                            IconButton(onClick = {
                                if (searchText.isNotEmpty()) {
                                    searchText = ""
                                    searchViewModel.onEvent(SearchEvent.OnExit("Search Screen"))
                                } else {
                                    searchBarActive = false
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }
                        }
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