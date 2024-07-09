package hiendao.moviefinder.presentation.movie

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.domain.model.movie.Movie
import hiendao.moviefinder.util.getAverageColor
import hiendao.moviefinder.util.getGenresFromCode
import kotlin.math.roundToInt

@Composable
fun MovieItemColumnScreen(
    modifier: Modifier = Modifier,
    movie: Movie,
    navigate: () -> Unit
) {

    val context = LocalContext.current

    val listGenres = getGenresFromCode(movie.genreIds).joinToString(" - ") { it.name }

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(makeFullUrl(movie.posterPath))
            .size(Size.ORIGINAL)
            .build()

    )
    val imageState = imagePainter.state

    val defaultDominantColor = MaterialTheme.colorScheme.primaryContainer
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(bottom = 10.dp, start = 8.dp, end = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        dominantColor
                    )
                )
            )
            .clickable {
                //Navigate
                navigate()
            }
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 130.dp)
                .fillMaxSize()
                .padding(6.dp)
        ) {

            when (imageState) {
                is AsyncImagePainter.State.Success -> {
                    val imageBitmap = imageState.result.drawable.toBitmap()
                    dominantColor = getAverageColor(imageBitmap = imageBitmap.asImageBitmap())
                    Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = "poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.background)
                    )
                }

                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.Center)
                            .scale(0.5f)
                    )
                }

                else -> {
                    dominantColor = MaterialTheme.colorScheme.primary
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "No image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(32.dp)
                            .alpha(0.8f),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 10.dp, horizontal = 5.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = movie.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.SentimentSatisfied,
                    contentDescription = "Vote average",
                    tint = Color.Green
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${(movie.voteAverage * 10).roundToInt()}%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Vote count",
                    tint = Color.Yellow
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${movie.voteCount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Text(
                text = listGenres,
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                color = Color.White
            )
        }
    }

}