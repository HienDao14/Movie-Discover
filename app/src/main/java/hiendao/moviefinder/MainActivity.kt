package hiendao.moviefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import hiendao.moviefinder.presentation.MainScreen
import hiendao.moviefinder.presentation.MovieViewModel
import hiendao.moviefinder.presentation.creditDetail.CreditScreen
import hiendao.moviefinder.presentation.creditDetail.CreditViewModel
import hiendao.moviefinder.presentation.favorite.FavoriteViewModel
import hiendao.moviefinder.presentation.movie.MoviesFullScreenWithPaged
import hiendao.moviefinder.presentation.movieDetail.MovieDetailScreen
import hiendao.moviefinder.presentation.movieDetail.MovieDetailViewModel
import hiendao.moviefinder.ui.theme.MovieFinderTheme
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.NavRoute
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieFinderTheme {
                Surface {
                    Navigation(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}


@Composable
fun Navigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    val mainViewModel = hiltViewModel<MovieViewModel>()
    val detailViewModel = hiltViewModel<MovieDetailViewModel>()
    val creditViewModel = hiltViewModel<CreditViewModel>()
    val favoriteViewModel = hiltViewModel<FavoriteViewModel>()

    val mainUIState = mainViewModel.mainUIState.collectAsState().value
    val detailState = detailViewModel.movieDetailState.collectAsState().value
    val creditState = creditViewModel.creditState.collectAsState().value
    val favoriteScreenState = favoriteViewModel.favoriteState.collectAsState().value

    NavHost(navController = navController, startDestination = NavRoute.HOME_SCREEN.name) {

        composable(
            route = NavRoute.HOME_SCREEN.name
        ) {
            MainScreen(
                modifier = modifier.fillMaxSize(),
                mainUIState = mainUIState,
                favoriteScreenState = favoriteScreenState,
                navHostController = navController,
                onEvent = mainViewModel::onEvent,
                onFavoriteEvent = favoriteViewModel::onEvent
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
            if(type == "Movie"){
                MoviesFullScreenWithPaged(
                    type = type,
                    navHostController = navController,
                    uiState = mainUIState,
                    onEvent = mainViewModel::onEvent
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
                detailViewModel.reload()
                detailViewModel.load(movieId.toInt())
                delay(500)
            }

            if(detailState.movie != null && detailState.movie.images.isNotEmpty() && detailState.listCredit.isNotEmpty()){
                MovieDetailScreen(
                    movie = detailState.movie,
                    detailState = detailState,
                    navHostController = navController,
                    onEvent = detailViewModel::onEvent
                )
            } else if(detailState.isLoading){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            else if(detailState.errorMsg != "") {
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

        composable(
            route = "${NavRoute.CREDIT_SCREEN}?creditId={creditId}",
            arguments = listOf(
                navArgument(name = "creditId"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            val creditId = entry.arguments?.getString("creditId")
            requireNotNull(creditId)

            LaunchedEffect(key1 = true) {
                creditViewModel.reload()
                creditViewModel.load(creditId = creditId.toInt())
                delay(2500)
            }

            CreditScreen(
                creditState = creditState,
                onEvent = creditViewModel::onEvent,
                navHostController = navController
            )
        }
    }
}