package hiendao.moviefinder.presentation.detail.videoSection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import hiendao.moviefinder.presentation.detail.movieDetail.WatchVideoScreen

@Composable
fun VideoSection(
    modifier: Modifier = Modifier,
    keys: List<String>,
    showVideo: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Video Section", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(keys.size){index ->
                val key = keys[index]
                WatchVideoScreen(
                    modifier = Modifier.width(350.dp).height(300.dp),
                    lifecycleOwner = lifecycleOwner,
                    videoId = key
                )
            }
        }
    }
}