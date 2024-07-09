package hiendao.moviefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import hiendao.moviefinder.presentation.MainScreen
import hiendao.moviefinder.presentation.MovieViewModel
import hiendao.moviefinder.presentation.detail.MovieDetailScreen
import hiendao.moviefinder.presentation.detail.MovieDetailViewModel
import hiendao.moviefinder.presentation.movie.MoviesFullScreenWithPaged
import hiendao.moviefinder.presentation.state.MainUIState
import hiendao.moviefinder.ui.theme.MovieFinderTheme
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.NavRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieFinderTheme {

                val mainViewModel = hiltViewModel<MovieViewModel>()
                val mainUIState = mainViewModel.mainUIState.collectAsState().value

                Surface {
                    Navigation(
                        modifier = Modifier.fillMaxSize(),
                        mainUIState = mainUIState
                    )
                }
            }
        }
    }
}


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    mainUIState: MainUIState
) {
    val navController = rememberNavController()

    val viewModel = hiltViewModel<MovieViewModel>()

    val detailViewModel = hiltViewModel<MovieDetailViewModel>()
    val detailState = detailViewModel.movieDetailState.collectAsState().value

    NavHost(navController = navController, startDestination = NavRoute.HOME_SCREEN.name) {

        composable(
            route = NavRoute.HOME_SCREEN.name
        ) {
            MainScreen(
                modifier = modifier.fillMaxSize(),
                mainUIState = mainUIState,
                navHostController = navController
            )
        }

        composable(
            route = "${NavRoute.LISTING_SCREEN.name}?type={type}",
            arguments = listOf(
                navArgument(name = "type"){
                    type = NavType.StringType
                }
            )
        ) {entry ->
            val type = entry.arguments?.getString("type")
            requireNotNull(type)
            if(type == Constant.moviesTrendingNowScreen){
                val movies = viewModel.popularMoviesPagedData.collectAsLazyPagingItems()
                MoviesFullScreenWithPaged(
                    movies = movies,
                    title = type,
                    navHostController = navController
                )
            }
        }

        composable(
            route = "${NavRoute.DETAIL_SCREEN}?movieId={movieId}",
            arguments = listOf(
                navArgument(name = "movieId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            val movieId = entry.arguments?.getString("movieId")
            requireNotNull(movieId)

            LaunchedEffect(key1 = true) {
                detailViewModel.load(movieId.toInt())
            }

            if(detailState.movie != null){
                MovieDetailScreen(
                    movie = detailState.movie,
                    detailState = detailState,
                    navHostController = navController
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Something went wrong",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp
                    )
                }
            }
        }
    }
}