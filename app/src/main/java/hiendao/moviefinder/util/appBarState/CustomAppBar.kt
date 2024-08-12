package hiendao.moviefinder.util.appBarState

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import hiendao.moviefinder.util.getAverageColor


private val MinToolbarHeight = 96.dp
private val MaxToolbarHeight = 176.dp

@Composable
fun CustomAppBar(
    imagePainter: AsyncImagePainter,
    progress: Float,
    modifier: Modifier = Modifier,
    title: String,
    limit: Boolean,
    onIconClick: () -> Unit
) {
    val imageState = imagePainter.state

    val defaultDominantColor = MaterialTheme.colorScheme.primaryContainer
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    if (limit) {
        Surface(
            color = Color.Transparent,
            modifier = modifier.background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        dominantColor
                    )
                )
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                when (imageState) {
                    is AsyncImagePainter.State.Success -> {

                        val imageBitmap = imageState.result.drawable.toBitmap()
                        dominantColor = getAverageColor(imageBitmap = imageBitmap.asImageBitmap())

                        Image(
                            bitmap = imageBitmap.asImageBitmap(),
                            contentDescription = "Trailer Video",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    alpha = progress * 0.75f
                                }
                                .drawWithContent {
                                    val colors = listOf(
                                        Color.Black,
                                        Color.Transparent
                                    )
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(colors),
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            alignment = BiasAlignment(0f, 1f - ((1f - progress) * 0.75f))
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
                                .graphicsLayer {
                                    alpha = progress * 0.75f
                                }
                                .align(
                                    alignment = BiasAlignment(
                                        0f,
                                        1f - ((1f - progress) * 0.75f)
                                    )
                                ),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (progress == 0f) {
                    Row(
                        modifier = Modifier.padding(top = 30.dp, start = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            onIconClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null
                            )
                        }
                        Text(
                            text = title,
                            fontSize = 20.sp, fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Box(modifier = Modifier.padding(top = 30.dp, start = 10.dp)) {
                        IconButton(
                            onClick = {
                                onIconClick()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}