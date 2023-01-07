package com.example.movieindex.feature.yt_player.helper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.example.movieindex.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import timber.log.Timber

class YtUiController(
    private val context: Context,
    private val customYtPlayerUi: View,
    private val ytPlayer: YouTubePlayer,
    private val ytPlayerView: YouTubePlayerView,
    private val ytScreenOrientationToggle: YtScreenOrientationToggle? = null,
    private val ytController: YtController,
) : YtPlayerListener(), YouTubePlayerFullScreenListener {

    private var isLandscape = false

    //    private val panel: View = customYtPlayerUi.findViewById(R.id.panel)
    private val playPauseButton: ImageView = customYtPlayerUi.findViewById(R.id.play_pause_button)
    private val loadingInd: ProgressBar = customYtPlayerUi.findViewById(R.id.loading_ind)
    private val timeStamp: LinearLayoutCompat = customYtPlayerUi.findViewById(R.id.timestamp)
    private val currentRuntime: TextView = customYtPlayerUi.findViewById(R.id.current_runtime)
    private val videoTimeSeparator: TextView =
        customYtPlayerUi.findViewById(R.id.video_time_separator)
    private val totalRuntime: TextView = customYtPlayerUi.findViewById(R.id.total_runtime)
    private val seekBar: AppCompatSeekBar = customYtPlayerUi.findViewById(R.id.seek_bar)

    override fun onApiChange(youTubePlayer: YouTubePlayer) {
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        val time = timeConversion(second.toInt())
        currentRuntime.text = time
    }

    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerError) {
        when (error) {
            PlayerError.UNKNOWN -> {}
            PlayerError.INVALID_PARAMETER_IN_REQUEST -> {}
            PlayerError.HTML_5_PLAYER -> {}
            PlayerError.VIDEO_NOT_FOUND -> {}
            PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> {}
        }
    }

    override fun onPlaybackQualityChange(
        youTubePlayer: YouTubePlayer,
        playbackQuality: PlaybackQuality,
    ) {
        when (playbackQuality) {
            PlaybackQuality.UNKNOWN -> {}
            PlaybackQuality.SMALL -> {}
            PlaybackQuality.MEDIUM -> {}
            PlaybackQuality.LARGE -> {}
            PlaybackQuality.HD720 -> {}
            PlaybackQuality.HD1080 -> {}
            PlaybackQuality.HIGH_RES -> {}
            PlaybackQuality.DEFAULT -> {}
        }
    }

    override fun onPlaybackRateChange(
        youTubePlayer: YouTubePlayer,
        playbackRate: PlaybackRate,
    ) {
        when (playbackRate) {
            PlaybackRate.UNKNOWN -> {}
            PlaybackRate.RATE_0_25 -> {}
            PlaybackRate.RATE_0_5 -> {}
            PlaybackRate.RATE_1 -> {}
            PlaybackRate.RATE_1_5 -> {}
            PlaybackRate.RATE_2 -> {}
        }
    }

    override fun onReady(youTubePlayer: YouTubePlayer) {
        loadingInd.isGone = true
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {

        loadingInd.isGone = state != PlayerState.BUFFERING

//        if (state == PlayerState.PLAYING || state == PlayerState.PAUSED || state == PlayerState.VIDEO_CUED) panel.setBackgroundColor(
//            ContextCompat.getColor(context,
//                android.R.color.transparent)) else if (state == PlayerState.BUFFERING) panel.setBackgroundColor(
//            ContextCompat.getColor(context, android.R.color.transparent))

        when (state) {
            PlayerState.UNKNOWN -> {}
            PlayerState.UNSTARTED -> {}
            PlayerState.ENDED -> {
                ytController.onVideoEnded()
            }
            PlayerState.PLAYING -> {
                playPauseButton.setOnClickListener {
                    youTubePlayer.pause()
                }
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_pause_48))
            }
            PlayerState.PAUSED -> {
                playPauseButton.setOnClickListener {
                    youTubePlayer.play()
                }
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_play_arrow_48))
            }
            PlayerState.BUFFERING -> {}
            PlayerState.VIDEO_CUED -> {}
        }
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        val time = timeConversion(duration.toInt())
        totalRuntime.text = time
        videoTimeSeparator.isGone = false
    }

    override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
        super.onVideoId(youTubePlayer, videoId)
    }

    override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
        super.onVideoLoadedFraction(youTubePlayer, loadedFraction)
        Timber.i("YtListener - onVideoLoadedFraction - loadedFraction: $loadedFraction")
    }

    // Full screen listener
    override fun onYouTubePlayerEnterFullScreen() {
        val viewParams = customYtPlayerUi.layoutParams
        viewParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        customYtPlayerUi.layoutParams = viewParams
        Timber.i("orientation - onYouTubePlayerEnterFullScreen before - isLandscape: $isLandscape")
        isLandscape = true
        Timber.i("orientation - onYouTubePlayerEnterFullScreen after - isLandscape: $isLandscape")
    }

    override fun onYouTubePlayerExitFullScreen() {
        val viewParams = customYtPlayerUi.layoutParams
        viewParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        viewParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        customYtPlayerUi.layoutParams = viewParams
        Timber.i("orientation - onYouTubePlayerExitFullScreen before - isLandscape: $isLandscape")
        isLandscape = false
        Timber.i("orientation - onYouTubePlayerExitFullScreen after - isLandscape: $isLandscape")
    }

    private fun timeConversion(totalSeconds: Int): String {
        val minutesInAnHour = 60
        val secondsInAnMinute = 60
        val seconds = totalSeconds % secondsInAnMinute
        val totalMinutes = totalSeconds / secondsInAnMinute
        val minutes = totalMinutes % minutesInAnHour
        val hours = totalMinutes / minutesInAnHour

        val hourString = hours.toString()
        val minutesString = String.format("%02d", minutes)
        val secondsString = String.format("%02d", seconds)
        return "$hourString:$minutesString:$secondsString"
    }
}