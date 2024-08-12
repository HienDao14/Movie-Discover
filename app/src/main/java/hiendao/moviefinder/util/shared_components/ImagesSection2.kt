package hiendao.moviefinder.util.shared_components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import hiendao.moviefinder.data.mapper.makeFullUrl
import hiendao.moviefinder.util.saveImageToExternalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ImagesSection2(
    modifier: Modifier = Modifier,
    images: List<String>,
    setShowImage: (Boolean) -> Unit,
    isPoster: Boolean
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        images.size
    }

    var imageIndex by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current

    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(images.size)
    )
    val requestPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Toast.makeText(context, "Permission granted: $isGranted", Toast.LENGTH_SHORT).show()
        }
    )

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(makeFullUrl(images[imageIndex]))
            .size(Size.ORIGINAL)
            .build()
    )

    val imageState = imagePainter.state

    Dialog(
        onDismissRequest = { setShowImage(false) },
//        properties = DialogProperties(
//            dismissOnBackPress = true,
//            dismissOnClickOutside = true,
//            usePlatformDefaultWidth = false
//        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { setShowImage(false) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Thoát"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(
                        onClick = {

                            var hasWriteExternalStorage = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                            val minSdk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

                            if (!hasWriteExternalStorage && minSdk) {
                                hasWriteExternalStorage = true
                            }

                            if (!hasWriteExternalStorage) {
                                requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                            if (hasWriteExternalStorage) {
                                if (saveImageToExternalStorage(
                                        context,
                                        UUID.randomUUID().toString(),
                                        if (imageState is AsyncImagePainter.State.Success) imageState.result.drawable.toBitmap()
                                        else null
                                    )
                                ) {
                                    Toast.makeText(
                                        context,
                                        "Image saved successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to save image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Thoát"
                        )
                    }
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Thoát"
                        )
                    }
                }
            }

            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
                flingBehavior = fling,
                key = {
                    images[it]
                },
                pageSize = PageSize.Fill
            ) {index ->
                imageIndex = index
                if(isPoster){
                    CustomImage(
                        imageUrl = images[index],
                        width = 300.dp,
                        height = 500.dp,
                        onClick = {

                        }
                    )
                } else {
                    CustomImage(
                        imageUrl = images[index],
                        width = 350.dp,
                        height = 200.dp,
                        onClick = {

                        }
                    )
                }
            }
        }
    }
}