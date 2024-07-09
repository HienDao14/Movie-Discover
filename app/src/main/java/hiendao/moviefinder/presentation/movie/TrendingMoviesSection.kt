package hiendao.moviefinder.presentation.movie

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.util.Constant
import hiendao.moviefinder.util.NavRoute

@Composable
fun TrendingMoviesSection(
    modifier: Modifier = Modifier,
    movies: List<Movie>,
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

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            items(movies.size) { index ->
                val movie = movies[index]
                AsyncImage(
                    model = makeFullUrl(movie.posterPath),
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(240.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(end = 12.dp)
                        .clickable {
                            //Navigate to detail screen of movie
                            navHostController.navigate("${NavRoute.DETAIL_SCREEN}?movieId=${movie.id}")
                        }
                )
            }
        }
    }
    //Trending Day
    //Special (Popular)
    //Recommend (Top Rated)
}