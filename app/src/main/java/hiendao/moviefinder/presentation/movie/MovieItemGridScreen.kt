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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.domain.model.Media
import hiendao.moviefinder.domain.model.Movie
import hiendao.moviefinder.ui.theme.MovieFinderTheme
import hiendao.moviefinder.util.getAverageColor
import hiendao.moviefinder.util.convert.getGenresFromCode
import hiendao.moviefinder.util.shared_components.RatingBar

@Composable
fun MovieItemGridScreen(
    modifier: Modifier = Modifier,
    media: Media,
    navigate: () -> Unit
) {
    val context = LocalContext.current
    val listGenres = getGenresFromCode(media.genreIds).joinToString(" - ") { it.name }

    val imageUrl = makeFullUrl(media.posterPath)
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    val defaultDominantColor = MaterialTheme.colorScheme.primaryContainer
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        modifier = modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    navigate()
                }
        ) {
            Box(
                modifier = Modifier
                    .height(240.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = media.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = listGenres,
                fontWeight = FontWeight.Normal,
                fontSize = 12.5.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 12.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingBar(
                    starsModifier = Modifier.size(18.dp),
                    rating = media.voteAverage / 2
                )
                Text(
                    text = media.voteAverage.toString().take(3),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Preview
@Composable
fun MovieItemPreview() {
    MovieFinderTheme {
        MovieItemGridScreen(
            media = Media(
                id = 1,
                backdropPath = "",
                genreIds = listOf(28, 12, 878),
                posterPath = "/iADOJ8Zymht2JPMoy3R7xceZprc.jpg",
                title = "Furiosa: A Mad Max Saga",
                voteAverage = 7.712,
                voteCount = 1605,
                mediaType = "Movie"
            ),
            navigate = {}
        )
    }
}