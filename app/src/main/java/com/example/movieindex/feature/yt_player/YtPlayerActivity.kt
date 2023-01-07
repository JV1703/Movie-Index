package com.example.movieindex.feature.yt_player

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.movieindex.R
import com.example.movieindex.databinding.ActivityYtPlayerBinding
import com.example.movieindex.feature.yt_player.helper.YtController
import com.example.movieindex.feature.yt_player.helper.YtPlayerListener
import com.example.movieindex.feature.yt_player.helper.YtUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper

class YtPlayerActivity : AppCompatActivity(), YtController {

    companion object {
        const val SAVED_YT_KEY = "ytKey"
    }

    private lateinit var binding: ActivityYtPlayerBinding
    private lateinit var customYtPlayerUi: View
    private lateinit var fadeViewHelper: FadeViewHelper

    private var movieKey: String? = null

    private val options = IFramePlayerOptions.Builder().controls(1).rel(0).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYtPlayerBinding.inflate(layoutInflater)
        requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.main_bg)

        val bundle = intent.extras
        movieKey = bundle?.getString("ytKey")

        customYtPlayerUi =
            binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_yt_player_ui)
        binding.youtubePlayerView.enterFullScreen()
        fadeViewHelper = FadeViewHelper(customYtPlayerUi)

        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.addYouTubePlayerListener(ytListener)
        binding.youtubePlayerView.initialize(ytListener, options)

    }

    private val ytListener = object : YtPlayerListener() {

        override fun onReady(youTubePlayer: YouTubePlayer) {
            val customYtUiController = YtUiController(
                context = this@YtPlayerActivity,
                customYtPlayerUi = customYtPlayerUi,
                ytPlayer = youTubePlayer,
                ytPlayerView = binding.youtubePlayerView,
                ytController = this@YtPlayerActivity)

            youTubePlayer.addListener(listener = customYtUiController)
            binding.youtubePlayerView.addFullScreenListener(fullScreenListener = customYtUiController)

            movieKey?.let {
                youTubePlayer.loadOrCueVideo(lifecycle, it, 0f)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString(SAVED_YT_KEY, movieKey)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?,
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        movieKey = savedInstanceState?.getString(SAVED_YT_KEY)
    }

    override fun onVideoEnded() {
        binding.youtubePlayerView.release()
        finish()
    }

    override fun onStop() {
        super.onStop()
        binding.youtubePlayerView.release()
    }
}

/*code below is for testing sensors, have not fully fix bug*/

//class YtPlayerActivity : AppCompatActivity(), YtScreenOrientationToggle {
//
//    private lateinit var binding: ActivityYtPlayerBinding
//    private lateinit var customYtPlayerUi: View
//    private lateinit var fadeViewHelper: FadeViewHelper
//    private lateinit var orientationListener: OrientationEventListener
//
//    private val defaultKey: String = "kpjwc55Bp6I"
//    private var currentOrientation: Int? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityYtPlayerBinding.inflate(layoutInflater)
//        currentOrientation = resources.configuration.orientation
//        setContentView(binding.root)
//
//        supportActionBar?.hide()
//        window.statusBarColor = ContextCompat.getColor(this, R.color.main_bg)
//
//        Timber.i("currentOrientation $currentOrientation")
//        orientationListener = object : OrientationEventListener(this) {
//            override fun onOrientationChanged(orientation: Int) {
//                /*Orientation Change Note*/
//                /*Auto Landscape at <293 and 67>*/
//                /*Auth Portrait at 337> and <23*/
//                /*estimate margin of 65 degrees to trigger rotation*/
//
//                val isPortrait = orientation >= 350 || orientation <= 10 || orientation in 170.. 190
//                val isLandscapeFacingWest = orientation in 260..280
//                val isLandscapeFacingEast = orientation in 80 .. 100
//
////                Timber.i("orientation: $orientation, requestedOrientation: $requestedOrientation, currentOrientation: ${resources.configuration.orientation}, isPortrait: $isPortrait, isLandscapeFacingWest: $isLandscapeFacingWest, isLandscapeFacingEast: $isLandscapeFacingEast")
//
//
//                if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && isLandscapeFacingEast || isLandscapeFacingWest){
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
//                }else if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && isPortrait){
//                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
//                }
//
//            }
//        }
//
//        customYtPlayerUi =
//            binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_yt_player_ui)
//        fadeViewHelper = FadeViewHelper(customYtPlayerUi)
//
//        lifecycle.addObserver(binding.youtubePlayerView)
//        binding.youtubePlayerView.addYouTubePlayerListener(ytListener)
//        binding.youtubePlayerView.initialize(ytListener, options)
//
//    }
//
//    private val options = IFramePlayerOptions.Builder().controls(0).build()
//
//    private val ytListener = object : YtPlayerListener() {
//
//        override fun onReady(youTubePlayer: YouTubePlayer) {
//            val customYtUiController = YtUiController(
//                context = this@YtPlayerActivity,
//                customYtPlayerUi = customYtPlayerUi,
//                ytPlayer = youTubePlayer,
//                ytPlayerView = binding.youtubePlayerView,
//                ytScreenOrientationToggle = this@YtPlayerActivity)
//
//            youTubePlayer.addListener(listener = customYtUiController)
//            binding.youtubePlayerView.addFullScreenListener(fullScreenListener = customYtUiController)
//
//            youTubePlayer.loadOrCueVideo(lifecycle, defaultKey, 0f)
//        }
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            binding.youtubePlayerView.enterFullScreen()
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            binding.youtubePlayerView.exitFullScreen()
//        }
//
//    }
//
//    override fun setLandscape() {
//        currentOrientation = 2
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//    }
//
//    override fun setPortrait() {
//        currentOrientation = 1
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if(isAutoRotateOn()){
//            orientationListener.enable()
//        }else{
//            orientationListener.disable()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        orientationListener.disable()
//    }
//
//    private fun isAutoRotateOn(): Boolean{
//        return Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
//    }
//}