package com.example.movieindex.feature.yt_player

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.movieindex.R
import com.example.movieindex.core.common.extensions.makeToast
import com.example.movieindex.core.data.remote.DevCredentials.YT_API_KEY
import com.example.movieindex.databinding.ActivityYtPlayerBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import timber.log.Timber

class YtPlayerActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener,
    YouTubePlayer.PlayerStateChangeListener {

    companion object {
        const val SAVED_YT_KEY = "ytKey"
    }

    private var movieKey: String? = null

    private lateinit var binding: ActivityYtPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYtPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_bg)

        val bundle = intent.extras
        movieKey = bundle?.getString(SAVED_YT_KEY)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        binding.youtubePlayer.initialize(YT_API_KEY, this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean,
    ) {
        p1?.setPlayerStateChangeListener(this)
        p1?.loadVideo(movieKey)
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?,
    ) {
        makeToast("Unable to load video")
        Timber.e("YoutubePlayer - ${p1?.name}")
    }

    override fun onLoading() {
    }

    override fun onLoaded(p0: String?) {
    }

    override fun onAdStarted() {
    }

    override fun onVideoStarted() {
    }

    override fun onVideoEnded() {
        this.finish()
    }

    override fun onError(p0: YouTubePlayer.ErrorReason?) {
        p0?.let {
            makeToast(it.name)
        }
    }
}