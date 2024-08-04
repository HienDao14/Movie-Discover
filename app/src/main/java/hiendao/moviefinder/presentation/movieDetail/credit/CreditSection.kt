package hiendao.moviefinder.presentation.movieDetail.credit

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
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
import hiendao.moviefinder.domain.model.Credit

@Composable
fun CreditSection(
    modifier: Modifier = Modifier,
    movieId: Int,
    credits: List<Credit>,
    navigate: (Int) -> Unit
) {
    val directors = credits.filter { it.job == "Director" }
    val actors = credits.filter { it.job != "Director" }

    val directorText = directors.joinToString(",") { it.name }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Director: ", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = directorText, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Actor: ", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(actors.size) { index ->
                val credit = credits[index]
                CreditItem(movieId = movieId, credit = credit) { creditId ->
                    //Navigate to credit detail
                    navigate(creditId)
                }
            }
        }
    }
}

@Composable
fun CreditItem(
    modifier: Modifier = Modifier,
    movieId: Int,
    credit: Credit,
    navigate: (Int) -> Unit
) {

    val movieIndex = credit.movieId.indexOf(movieId.toString())

    if(movieIndex >= credit.character.size || movieIndex == -1){
        println("${credit.movieId.get(movieIndex)} in $movieIndex")
        println(credit.character.size)
        return
    }

    val character = credit.character[movieIndex]

    val context = LocalContext.current

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(makeFullUrl(credit.profilePath))
            .size(Size.ORIGINAL)
            .build()
    )
    val imageState = imagePainter.state

    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable {
                navigate(credit.id)
            }
    ) {

        Card(
            modifier = modifier
                .size(width = 120.dp, height = 220.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (imageState) {
                    is AsyncImagePainter.State.Success -> {
                        Image(
                            bitmap = imageState.result.drawable.toBitmap().asImageBitmap(),
                            contentDescription = "Trailer Video",
                            contentScale = ContentScale.Crop
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
        }

        Text(
            text = credit.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = character,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic
        )
    }
}