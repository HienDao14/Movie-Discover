package hiendao.moviefinder.util.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppbarWithImage(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap
) {

    val scrollState : ScrollState = rememberScrollState()

    val headerHeight = 200.dp
    val toolbarHeight = 56.dp

    val toolbarBottomOffset by remember {
        mutableStateOf( 200.dp - 56.dp )
    }

    val showToolbar by remember {
        derivedStateOf {
            scrollState.value >= toolbarBottomOffset.value
        }
    }



    Box(modifier = modifier) {
        //Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .graphicsLayer {
                    translationY = -scrollState.value.toFloat() / 2f
                    alpha = (-1f / 200.dp.toPx()) * scrollState.value + 1
                }
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Backdrop",
                contentScale = ContentScale.FillBounds
            )
        }

        //Body
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            repeat(5){
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(16.dp)
                )
            }

        }

        //Toolbar
        AnimatedVisibility(
            visible = showToolbar,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            
        }
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            navigationIcon = {
                IconButton(onClick = {  }, modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)) {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "")
                }
            },
            title = { }
        )

        //Title
        Text(text = "Movie name is asdasdasdfffffffff", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
    }
}
