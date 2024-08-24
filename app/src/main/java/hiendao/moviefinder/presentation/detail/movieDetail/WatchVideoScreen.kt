package hiendao.moviefinder.presentation.detail.movieDetail

import android.view.View.NOT_FOCUSABLE
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import hiendao.moviefinder.R
import hiendao.moviefinder.presentation.detail.videoSection.CustomVideoUIController


@Composable
fun WatchVideoScreen(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner,
    videoId: String
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { context ->

            YouTubePlayerView(context = context).apply {
                lifecycleOwner.lifecycle.addObserver(this)
                val customUI = this.inflateCustomPlayerUi(R.layout.layout)
                this.enableAutomaticInitialization = false

                val listener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        val customController = CustomVideoUIController(
                            youTubePlayer, customUI, context
                        )
                        youTubePlayer.addListener(customController)
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                }
                val options = IFramePlayerOptions.Builder()
                    .controls(0).build()

                this.initialize(listener, options)
            }
        })
    }
}