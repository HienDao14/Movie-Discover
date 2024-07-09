package hiendao.moviefinder.util.appBarState

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val MinToolbarHeight = 96.dp
private val MaxToolbarHeight = 176.dp

@Composable
private fun rememberToolbarState(toolbarHeightRange: IntRange): ToolbarState {
    return rememberSaveable(saver = ExitUntilCollapsedState.Saver) {
        ExitUntilCollapsedState(toolbarHeightRange)
    }
}

@Composable
fun CustomAppBar(
    imageBitmap: ImageBitmap,
    progress: Float,
    modifier: Modifier = Modifier,
    title: String
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = progress * 0.75f
                    },
                alignment = BiasAlignment(0f, 1f - ((1f - progress) * 0.75f))
            )
            if(progress == 0f){
                val context = LocalContext.current
                Row(modifier = Modifier.padding(top = 30.dp, start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        Toast.makeText(context, "Back", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
                    }
                    Text(
                        text = title,
                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}