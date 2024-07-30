package hiendao.moviefinder.presentation.movieDetail.images

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hiendao.moviefinder.util.shared_components.CustomImage

@Composable
fun ImageSection(
    modifier: Modifier = Modifier,
    images: List<String>,
    showImage: (String, String) -> Unit
) {
    val posters = mutableListOf<String>()
    val backdrops = mutableListOf<String>()
    if (images[0].isNotEmpty()) {
        posters.addAll(images[0].split(","))
    }

    if (images[1].isNotEmpty()) {
        backdrops.addAll(images[1].split(","))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
    ) {
        if (posters.isNotEmpty()) {
            Text(text = "Poster", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(posters.size) { index ->
                    CustomImage(
                        imageUrl = posters[index],
                        width = 120.dp,
                        height = 180.dp,
                        onClick = {
                            showImage(posters[index], "Poster")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (backdrops.isNotEmpty()) {
            Text(text = "Backdrops", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(backdrops.size) { index ->
                    CustomImage(
                        imageUrl = backdrops[index],
                        width = 200.dp,
                        height = 150.dp,
                        onClick = {
                            showImage(backdrops[index], "Backdrops")
                        })
                }
            }
        }
    }
}