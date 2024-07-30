package hiendao.moviefinder.presentation.movie

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.domain.model.TvSeries
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.NavRoute
import hiendao.moviefinder.util.shared_components.CustomImage

@Composable
fun TrendingMoviesSection(
    modifier: Modifier = Modifier,
    movies: List<Movie>,
    listTvSeries: List<TvSeries>,
    navHostController: NavHostController,
    title: String
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "See All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.clickable {
                    //Navigate to MoviesFullScreenWithPaged
                    navHostController.navigate("${NavRoute.LISTING_SCREEN.name}?type=${Constant.moviesTrendingNowScreen}")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(movies.isNotEmpty()){
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(movies.size){index ->
                    val movie = movies[index]
                    CustomImage(
                        imageUrl = movie.posterPath,
                        width = 150.dp,
                        height = 240.dp,
                        onClick = {
                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movie.id}")
                        }
                    )
                }
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listTvSeries.size){ index ->
                    val tvSeries = listTvSeries[index]
                    CustomImage(
                        imageUrl = tvSeries.posterPath,
                        width = 150.dp,
                        height = 240.dp,
                        onClick = {
//                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${tvSeries.id}")
                        }
                    )
                }
            }
        }
    }
    //Trending Day
    //Special (Popular)
    //Recommend (Top Rated)
}