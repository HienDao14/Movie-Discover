package hiendao.moviefinder.presentation.detail.videoSection

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import hiendao.moviefinder.R

class CustomVideoUIController(
    private val youTubePlayer: YouTubePlayer,
    private val customView: View,
    private val context: Context
) : AbstractYouTubePlayerListener() {
    val panel = customView.findViewById<View>(R.id.panel)
    val button = customView.findViewById<ImageView>(R.id.btnPlay)
    init {
        val tracker = YouTubePlayerTracker()
        youTubePlayer.addListener(tracker)
        button.setOnClickListener {
            if(tracker.state == PlayerConstants.PlayerState.PLAYING) youTubePlayer.pause()
            else youTubePlayer.play()
        }
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {

    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        if (state == PlayerConstants.PlayerState.PLAYING || state == PlayerConstants.PlayerState.PAUSED || state == PlayerConstants.PlayerState.VIDEO_CUED)
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        else if (state == PlayerConstants.PlayerState.BUFFERING)
            panel.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }
}