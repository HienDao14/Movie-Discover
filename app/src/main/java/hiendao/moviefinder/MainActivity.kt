package hiendao.moviefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
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
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import eu.kanade.presentation.webview.WebViewScreenContent
import hiendao.moviefinder.presentation.main.MainScreen
import hiendao.moviefinder.presentation.main.MainViewModel
import hiendao.moviefinder.presentation.detail.creditDetail.CreditScreen
import hiendao.moviefinder.presentation.detail.creditDetail.CreditViewModel
import hiendao.moviefinder.presentation.main.favorite.FavoriteViewModel
import hiendao.moviefinder.presentation.pagedScreen.MoviesFullScreenWithPaged
import hiendao.moviefinder.presentation.detail.movieDetail.MovieDetailScreen
import hiendao.moviefinder.presentation.detail.movieDetail.MovieDetailViewModel
import hiendao.moviefinder.presentation.pagedScreen.TvSeriesFullScreenWithPaged
import hiendao.moviefinder.presentation.detail.tvSeriesDetail.SeriesDetailViewModel
import hiendao.moviefinder.presentation.detail.tvSeriesDetail.TvSeriesDetailScreen
import hiendao.moviefinder.ui.theme.MovieFinderTheme
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.worker.SyncDataWorker
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var workManager : WorkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        workManager = WorkManager.getInstance(applicationContext)
        val syncRequest = PeriodicWorkRequestBuilder<SyncDataWorker>(Duration.ofHours(1))
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5000L, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
//
//        val syncRequest = OneTimeWorkRequestBuilder<SyncDataWorker>()
//            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
//            .setInitialDelay(Duration.ofSeconds(10))
//            .build()

        setContent {
            MovieFinderTheme {
                Surface {
                    workManager.enqueueUniquePeriodicWork(
                        "sync_data",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        syncRequest
                    )
//                    workManager.enqueueUniqueWork(
//                        "sync_data",
//                        ExistingWorkPolicy.REPLACE,
//                        syncRequest
//                    )
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

    val mainViewModel = hiltViewModel<MainViewModel>()
    val movieDetailViewModel = hiltViewModel<MovieDetailViewModel>()
    val seriesDetailViewModel = hiltViewModel<SeriesDetailViewModel>()
    val creditViewModel = hiltViewModel<CreditViewModel>()
    val favoriteViewModel = hiltViewModel<FavoriteViewModel>()

    val mainUIState = mainViewModel.mainUIState.collectAsState().value
    val movieDetailState = movieDetailViewModel.movieDetailState.collectAsState().value
    val seriesDetailState = seriesDetailViewModel.seriesState.collectAsState().value
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
                navArgument(name = "type") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val type = entry.arguments?.getString("type")
            requireNotNull(type)
            if (type == "Movie") {
                MoviesFullScreenWithPaged(
                    type = type,
                    navHostController = navController,
                    uiState = mainUIState,
                    onEvent = mainViewModel::onEvent
                )
            } else {
                TvSeriesFullScreenWithPaged(
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
                navArgument(name = "movieId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val movieId = entry.arguments?.getString("movieId")
            requireNotNull(movieId)

            LaunchedEffect(key1 = true) {
                movieDetailViewModel.reload()
                movieDetailViewModel.load(movieId.toInt())
                delay(500)
            }

            if (movieDetailState.movie != null && movieDetailState.movie.images.isNotEmpty() && movieDetailState.listCredit.isNotEmpty()) {
                MovieDetailScreen(
                    movie = movieDetailState.movie,
                    detailState = movieDetailState,
                    navHostController = navController,
                    onEvent = movieDetailViewModel::onEvent
                )
            } else if (movieDetailState.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 60.dp)){
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(height = 50.dp, width = 200.dp)
                            .align(Alignment.TopCenter)
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
            } else if (movieDetailState.errorMsg.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 60.dp)){
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(height = 50.dp, width = 200.dp)
                            .align(Alignment.TopCenter)
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
            }
        }

        composable(
            route = "${NavRoute.DETAIL_SCREEN}?seriesId={seriesId}",
            arguments = listOf(
                navArgument(name = "seriesId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val seriesId = entry.arguments?.getString("seriesId")
            requireNotNull(seriesId)

            LaunchedEffect(key1 = true) {
                seriesDetailViewModel.reload()
                seriesDetailViewModel.load(id = seriesId.toInt())
                delay(500)
            }

            if (seriesDetailState.series != null && seriesDetailState.series.images.isNotEmpty() && seriesDetailState.credits.isNotEmpty()) {
                //Series Screen Detail
                TvSeriesDetailScreen(
                    series = seriesDetailState.series,
                    seriesDetailState = seriesDetailState,
                    navHostController = navController,
                    onEvent = seriesDetailViewModel::onEvent
                )
            } else if (seriesDetailState.isLoading) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 60.dp)){
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(height = 50.dp, width = 200.dp)
                            .align(Alignment.TopCenter)
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
            } else if (!seriesDetailState.errorMsg.isNullOrEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 60.dp)){
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(height = 50.dp, width = 200.dp)
                            .align(Alignment.TopCenter)
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
            }
        }

        composable(
            route = "${NavRoute.CREDIT_SCREEN}?creditId={creditId}",
            arguments = listOf(
                navArgument(name = "creditId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
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

        composable(
            route = "${NavRoute.WEB_VIEW}?url={url}",
            arguments = listOf(
                navArgument(name = "url"){
                    type = NavType.StringType
                }
            )
        ){entry ->
            val url = entry.arguments?.getString("url")
            requireNotNull(url)

            WebViewScreenContent(
                onNavigateUp = {
                    navController.navigateUp()
                },
                initialTitle = "WebView",
                url = url,
                onUrlChange = {}
            )
        }
    }
}