package hiendao.moviefinder.presentation.movie

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.ListingType
import hiendao.moviefinder.util.NavRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesFullScreenWithPaged(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<Movie>,
    title: String,
    navHostController: NavHostController
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = movies.loadState) {
        if (movies.loadState.refresh is LoadState.Error) {
            Log.e(
                "paging_error",
                "Error: " + (movies.loadState.refresh as LoadState.Error).error.message
            )
        }
    }

    var listingType by remember {
        mutableStateOf(ListingType.GRID)
    }
    val screenTitle = if (title == Constant.moviesTrendingNowScreen) "Movies - Trending Now"
    else "Tv Series"
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    listingType = ListingType.GRID
                }) {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "Grid view",
                        tint = if (listingType == ListingType.GRID) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {
                    listingType = ListingType.COLUMN
                }) {
                    Icon(
                        imageVector = Icons.Default.TableRows,
                        contentDescription = "Column view",
                        tint = if (listingType == ListingType.COLUMN) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = modifier) {
                if (movies.loadState.refresh is LoadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    if (listingType == ListingType.GRID) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(movies.itemCount) {
                                val movie = movies[it]
                                if (movie != null) {
                                    MovieItemGridScreen(
                                        movie = movie,
                                        navigate = {
                                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movie.id}")
                                        })
                                }
                            }

                            item {
                                if (movies.loadState.append is LoadState.Loading) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(movies.itemCount) {
                                val movie = movies[it]
                                if (movie != null) {
                                    MovieItemColumnScreen(
                                        movie = movie,
                                        navigate = {
                                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movie.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}