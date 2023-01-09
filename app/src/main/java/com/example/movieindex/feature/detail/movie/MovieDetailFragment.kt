package com.example.movieindex.feature.detail.movie

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.example.movieindex.R
import com.example.movieindex.core.common.*
import com.example.movieindex.core.common.extensions.*
import com.example.movieindex.core.data.external.*
import com.example.movieindex.core.data.remote.NetworkConstants.BACKDROP_SIZE
import com.example.movieindex.core.data.remote.NetworkConstants.BASE_IMG_URL
import com.example.movieindex.core.data.remote.NetworkConstants.CREDIT_IMG_SIZE
import com.example.movieindex.core.data.remote.NetworkConstants.POSTER_SIZE_LARGE
import com.example.movieindex.databinding.FragmentMovieDetailBinding
import com.example.movieindex.feature.detail.movie.adapter.MovieDetailCastAdapter
import com.example.movieindex.feature.detail.movie.adapter.MovieDetailRecommendationAdapter
import com.example.movieindex.feature.detail.movie.adapter.MovieVideosAdapter
import com.example.movieindex.feature.list.movie_list.ListType
import com.example.movieindex.feature.yt_player.YtPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import javax.inject.Inject


@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    @Inject
    lateinit var colorPalette: ColorPalette

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val navArgs: MovieDetailFragmentArgs by navArgs()
    private var _movieId: Int? = null
    private val movieId get() = _movieId!!

    private val viewModel: MovieDetailViewModel by viewModels()

    private lateinit var castAdapter: MovieDetailCastAdapter
    private lateinit var videosAdapter: MovieVideosAdapter
    private lateinit var recommendationAdapter: MovieDetailRecommendationAdapter

    private var casts: List<Cast> = emptyList()
    private var crews: List<Crew> = emptyList()
    private var movieDetails: MovieDetails? = null

    private var isOpen: Boolean = false

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        rotateForward =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fab_rotate_forward)
        rotateBackward =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fab_rotate_backward)

        navArgs.movieId.let {
            viewModel.saveMovieId(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCastAdapter()
        setupRecommendationsAdapter()
        setupVideosAdapter()

        binding.fabMain.setOnClickListener {
            toggleFab()
        }

        collectLatestLifecycleFlow(viewModel.uiState) { uiState ->

            uiState.userMsg?.let {
                makeToast(it)
                viewModel.msgShown()
            }

            isLoading(uiState.isLoading)

            val movieDetails = uiState.movieDetails
            val cachedMovie = uiState.cachedMovie
            movieDetails?.let { details ->
                _movieId = details.id
                this@MovieDetailFragment.movieDetails = movieDetails
                val casts = details.casts
                val crews = details.crews

                this@MovieDetailFragment.casts = casts
                this@MovieDetailFragment.crews = crews

                setupMovieGeneralDetails(movieDetails = details)

            }

            val isFavorite = cachedMovie?.isFavorite ?: false
            val isBookmarked = cachedMovie?.isBookmark ?: false

            val fabPlaylistIcon =
                if (isBookmarked) R.drawable.ic_filter_list_off_48 else R.drawable.ic_playlist_play_48
            binding.fabPlaylist.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                fabPlaylistIcon))
            val fabFavoriteIcon =
                if (isFavorite) R.drawable.ic_heart_minus_48 else R.drawable.ic_heart_plus_48
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                fabFavoriteIcon))

            binding.fabFavorite.setOnClickListener {
                if (cachedMovie == null) {
                    viewModel.insertMovie(
                        mediaId = movieId,
                        movieDetails = movieDetails!!, isFavorite = true)
                } else {
                    viewModel.updateFavorite(movieId = movieId,
                        isBookmarked = isBookmarked,
                        isFavorite = !isFavorite)
                }
                toggleFab()
            }

            binding.fabPlaylist.setOnClickListener {
                if (cachedMovie == null) {
                    viewModel.insertMovie(
                        mediaId = movieId,
                        movieDetails = movieDetails!!, isBookmarked = true)
                } else {
                    viewModel.updateBookmark(movieId = movieId,
                        isBookmarked = !isBookmarked,
                        isFavorite = isFavorite)
                }
                toggleFab()
            }
        }

        // lists needs to have a different collector is to apply distinct until change on list
        // the idea is that if uiState is updated, however the list doesn't change, therefore
        // flow will omit emission to list data.
        // thus avoiding unnecessary glide operations
        collectLatestLifecycleFlow(viewModel.uiState.map { it.movieDetails?.casts }
            .distinctUntilChanged()) { casts: List<Cast>? ->
            casts?.let {
                setupCastSection(it)
            }
        }


        // selection of reviews is through selecting a single review from reviews in a randomized manner
        // a distinctUntilChanged is required to avoid review to be randomly reselected everytime UI change occurs
        collectLatestLifecycleFlow(viewModel.uiState.map { it.movieDetails?.reviews }
            .distinctUntilChanged()) { reviews: List<ReviewResult>? ->
            reviews?.let {
                setupReviewSection(it)
            }
        }

        collectLatestLifecycleFlow(viewModel.uiState.map { it.movieDetails?.videos }
            .distinctUntilChanged()) { videos: List<VideosResult>? ->
            videos?.let {
                setupVideosSection(it)
            }
        }

        collectLatestLifecycleFlow(viewModel.uiState.map { it.movieDetails?.recommendations }) { recommendations: List<Result>? ->
            recommendations?.let {
                setupRecommendationSection(it)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recommendationRv.adapter = null
        binding.videosRv.adapter = null
        binding.castRv.adapter = null
        _binding = null
    }

    private fun isLoading(isLoading: Boolean) {
        if (!isLoading) {
            binding.nestedScrollView.animate().alpha(1F).apply {
                startDelay = 500
                duration = 500
            }

            binding.fabMain.apply {
                isEnabled = true
                animate().alpha(1F).apply {
                    startDelay = 500
                    duration = 500
                }
            }

            binding.fabFavorite.apply {
                isEnabled = true
                animate().alpha(1F).apply {
                    startDelay = 500
                    duration = 500
                }
            }

            binding.fabPlaylist.apply {
                isEnabled = true
                animate().alpha(1F).apply {
                    startDelay = 500
                    duration = 500
                }
            }
        } else {
            binding.nestedScrollView.alpha = 0F

            binding.fabMain.apply {
                isEnabled = true
                alpha = 0F
            }

            binding.fabFavorite.apply {
                isEnabled = true
                alpha = 0F
            }

            binding.fabPlaylist.apply {
                isEnabled = true
                alpha = 0F
            }
        }
        binding.loadingInd.isGone = !isLoading
    }

    private fun setTextColor(color: Int) {
        val luminanceValue = ColorUtils.calculateLuminance(color)
        val textColor = if (luminanceValue > 0.5) R.color.black else R.color.white
        binding.movieGeneralDetailsContainer.children.forEach { view ->
            if (view is TextView) {
                view.setColor(textColor)
            }
        }
        binding.userScore.setColor(textColor)
        binding.playTrailer.setColor(textColor)
        binding.playTrailer.compoundDrawables[0].setTint(ContextCompat.getColor(requireContext(),
            textColor))
    }

    private fun setupCastAdapter(/*casts: List<Cast>, crews: List<Crew>*/) {
        castAdapter = MovieDetailCastAdapter(
            onCastClicked = {},
            onViewMoreClicked = {
                navigateToCreditListFragment()
            }
        )
        castAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.castRv.apply {
            adapter = castAdapter
            setLayoutManager(layoutManager)
            setHasFixedSize(true)
        }

//        binding.castRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//
//                val firstCompletelyVisibleItemPosition =
//                    layoutManager.findFirstCompletelyVisibleItemPosition()
//                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                val lastCompletelyVisibleItemPosition =
//                    layoutManager.findLastCompletelyVisibleItemPosition()
//                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//
//                Timber.i("visible positions - firstCompletelyVisibleItemPosition: $firstCompletelyVisibleItemPosition, firstVisibleItemPosition: $firstVisibleItemPosition, lastCompletelyVisibleItemPosition: $lastCompletelyVisibleItemPosition, lastVisibleItemPosition: $lastVisibleItemPosition")
//
//                setCastsRvScrollableInd()
//
//                if (lastVisibleItemPosition > 2) {
//                    binding.scrollableInd.animate().alpha(0F).duration = 500
//                } else {
//                    binding.scrollableInd.animate().alpha(1F).duration = 500
//                }
//
//                super.onScrolled(recyclerView, dx, dy)
//            }
//        }
//        )
    }

    private fun setupVideosAdapter() {
        videosAdapter = MovieVideosAdapter { key ->
            watchTrailer(key)
        }
        videosAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        binding.videosRv.apply {
            val snapHelper = LinearSnapHelper()
            adapter = videosAdapter
            setHasFixedSize(true)
            onFlingListener = null
            snapHelper.attachToRecyclerView(this)
        }

    }

    private fun setupRecommendationsAdapter() {
        recommendationAdapter = MovieDetailRecommendationAdapter(
            onViewMoreClicked = {
                val action =
                    MovieDetailFragmentDirections.actionMovieDetailFragmentToMovieListFragment(
                        listType = ListType.Recommendation, recommendationMovieId = movieId)
                findNavController().navigate(action)
            },
            onRecommendationClicked = {
                viewModel.saveMovieId(it)
                binding.nestedScrollView.smoothScrollTo(0, 0)
                isOpen = true
                toggleFab()
            }
        )
        recommendationAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        binding.recommendationRv.apply {
            adapter = recommendationAdapter
            setHasFixedSize(true)
        }
    }

    private fun setCastsRvScrollableInd() {

        val gradientBg: GradientDrawable =
            when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(R.color.black,
                            addAlpha(0.5F, R.color.black),
                            android.R.color.transparent))
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(R.color.white,
                            addAlpha(0.5F, R.color.white),
                            android.R.color.transparent))
                }
                else -> {
                    GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(R.color.black,
                            addAlpha(0.5F, R.color.black),
                            android.R.color.transparent))
                }
            }

        binding.scrollableInd.background = gradientBg
    }

    private fun getAvatarPath(path: String): String {
        val output =
            if (path.contains("gravatar")) "https://${path.substringAfter("//")}" else BASE_IMG_URL + CREDIT_IMG_SIZE + path
        return output
    }

    private fun toggleReviewContentTvSize(textView: TextView) {
        if (textView.lineCount == textView.maxLines) {
            textView.maxLines = Int.MAX_VALUE
        } else {
            textView.maxLines = 5
        }
    }

    private fun toggleExpandIvRotation(imageView: ImageView) {
        val defaultRotation = 0F
        if (imageView.rotation == defaultRotation) {
            imageView.animate().rotation(180F).start()
        } else {
            imageView.animate().rotation(0F).start()
        }
    }

    private fun setupMovieGeneralDetails(movieDetails: MovieDetails) {
        val defaultDominantColor = Color.parseColor("#263238")

        val trailerKey = if(movieDetails.videos.isNotEmpty()){
            movieDetails.videos.random().key
        }else{
            null
        }
        binding.playTrailer.setOnClickListener {
            trailerKey?.let {
                watchTrailer(it)
            }?:viewModel.showMsg("No trailer for this movie")
        }

        movieDetails.posterPath?.let {
            binding.moviePoster.loadImage(BASE_IMG_URL + POSTER_SIZE_LARGE + movieDetails.posterPath,
                onLoadFailed = {

                    binding.posterContainer.background =
                        GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                            intArrayOf(defaultDominantColor,
                                defaultDominantColor,
                                android.R.color.transparent))

                    binding.movieGeneralDetailsContainer.setBackgroundColor(
                        defaultDominantColor)

                    setTextColor(defaultDominantColor)
                    binding.someContainer.setBackgroundColor(colorChanger(defaultDominantColor,
                        0.8F))

                    viewModel.updatePosterLoadingStatus(false)
                },
                onLoadFinish = { resource ->
                    resource!!.let { drawable ->
                        colorPalette.calcDominantColor(drawable) { color ->

                            binding.posterContainer.background =
                                GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                                    intArrayOf(color,
                                        color,
                                        android.R.color.transparent))

                            binding.movieGeneralDetailsContainer.setBackgroundColor(
                                color)

                            setTextColor(color)
                            binding.someContainer.setBackgroundColor(colorChanger(color, 0.8F))

                            viewModel.updatePosterLoadingStatus(false)
                        }
                    }
                })
        } ?: binding.moviePoster.loadErrorImage()

        movieDetails.backdropPath?.let {
            binding.movieBackdrop.loadImageRoundedCorner(source = BASE_IMG_URL + BACKDROP_SIZE + movieDetails.backdropPath,
                radius = 8)
        } ?: binding.movieBackdrop.loadErrorImage()

        binding.movieTitle.text = movieDetails.title

        val movieRating = (movieDetails.voteAverage?.times(10))?.toInt()

        binding.ratingInd.apply {
            max = 100

            if (movieRating == null) {
                progress = 0
            } else {
                progress = movieRating

                setIndicatorColor(ContextCompat.getColor(this.context,
                    getMovieRatingIndicatorColor(movieRating)))
                trackColor =
                    ContextCompat.getColor(this.context, getMovieRatingTrackColor(movieRating))

            }
        }
        binding.ratingTv.apply {
            if (movieRating == null) {
                text = "NR"
            } else {
                text = "${movieRating}%"
                toSuperScript(startIndex = text.indexOf('%'), endIndex = text.length)
            }
        }

        if (movieDetails.mpaaRating.isNullOrEmpty()) {
            binding.mpaaRating.isGone = true
        } else {
            binding.mpaaRating.text = movieDetails.mpaaRating
        }

        if (movieDetails.releaseDate.isNullOrEmpty()) {
            binding.movieReleaseDate.text = ""
            binding.movieReleaseDate.isInvisible = true
        } else {
            binding.movieReleaseDate.text = movieDetails.releaseDate
        }

        if (movieDetails.runtime == null) {
            binding.bulletSeparator.isGone = true
            binding.runtime.isGone = true
        } else {
            val runtime: String = if (movieDetails.runtime < 60) {
                "${movieDetails.runtime}m"
            } else {
                val hour: Int = movieDetails.runtime / 60
                val minute: Int = movieDetails.runtime % 60
                "${hour}h ${minute}m"
            }
            binding.runtime.text = runtime
        }

        if (movieDetails.genres.isEmpty()) {
            binding.genre.isGone = true
        } else {
            binding.genre.text = movieDetails.genres.joinToString(separator = ", ") { it.name }
        }

        if (movieDetails.tagline.isNullOrEmpty()) {
            binding.tagline.isGone = true
        } else {
            movieDetails.tagline.let { binding.tagline.text = it }
        }

        binding.overview.text = movieDetails.overview ?: "No overview available"
    }

    private suspend fun setupCastSection(casts: List<Cast>/*, crews: List<Crew>*/) {
        if (casts.isEmpty()) {
            binding.castContainer.isGone = true
        } else {
            setupCastAdapter()
            castAdapter.submitList(viewModel.generateCastList(casts))
        }

        binding.creditTv.setOnClickListener {
            navigateToCreditListFragment()
        }
    }

    private fun setupReviewSection(reviews: List<ReviewResult>?) {
        if (reviews.isNullOrEmpty()) {
            binding.reviewContainer.isGone = true
        } else {
            val review = reviews.random()
            val author = review.author
            val rating = review.authorDetails?.rating
            val content = review.content
            val avatarPath = review.authorDetails?.avatarPath?.let { getAvatarPath(it) }
            val updatedAt = ZonedDateTime.parse(review.updatedAt).toLocalDateTime()
                .toString(pattern = "MMMM dd, yyyy")

            binding.reviewerImg.loadCircleImage(avatarPath,
                error = getCircleTextDrawable(text = author.first().uppercaseChar()
                    .toString(), color = getRandomColor(), fontSize = 64))

            if (rating == null) {
                binding.reviewRating.isGone = true
            } else {
                binding.reviewRating.text = rating.toString()
            }

            binding.reviewTitle.text = getString(R.string.review_title, author)
            binding.reviewDetails.text = getString(R.string.review_details,
                author,
                updatedAt)
            binding.reviewContent.text = content
            binding.expandToggleIv.isGone = binding.reviewContent.lineCount <= 5

            val isReviewContentShort =
                binding.reviewContent.lineCount < binding.reviewContent.maxLines
            if (isReviewContentShort) {
                binding.expandToggleIv.isGone = true
            } else {
                binding.expandToggleIv.isGone = false
                binding.expandToggleIv.setOnClickListener {
                    toggleExpandIvRotation(it as ImageView)
                    toggleReviewContentTvSize(binding.reviewContent)
                }
            }
        }
    }

    private fun setupVideosSection(videos: List<VideosResult>) {
        if (videos.isEmpty()) {
            binding.videosContainer.isGone = true
        } else {
            setupVideosAdapter()
            videosAdapter.submitList(videos)
        }
    }

    private suspend fun setupRecommendationSection(recommendations: List<Result>) {
        if (recommendations.isEmpty()) {
            binding.recommendationContainer.isGone = true
        } else {
            setupRecommendationsAdapter()
            recommendationAdapter.submitList(viewModel.generateRecommendationList(recommendations))
        }
    }

    private fun navigateToCreditListFragment() {
        viewModel.saveCasts(casts)
        viewModel.saveCrews(crews)
        val action =
            MovieDetailFragmentDirections.actionMovieDetailFragmentToCreditListFragment()
        findNavController().navigate(action)
    }

    private fun watchTrailer(key: String) {
        val intent = Intent(requireContext(), YtPlayerActivity::class.java)
        intent.putExtra("ytKey", key)
        startActivity(intent)
    }

    private fun toggleFab() {
        if (isOpen) {
            binding.fabMain.startAnimation(rotateBackward)
            binding.fabFavorite.startAnimation(fabClose)
            binding.fabPlaylist.startAnimation(fabClose)
            binding.fabFavorite.isClickable = false
            binding.fabPlaylist.isClickable = false
            this@MovieDetailFragment.isOpen = false
        } else {
            binding.fabMain.startAnimation(rotateForward)
            binding.fabFavorite.startAnimation(fabOpen)
            binding.fabPlaylist.startAnimation(fabOpen)
            binding.fabFavorite.isClickable = true
            binding.fabPlaylist.isClickable = true
            this@MovieDetailFragment.isOpen = true
        }
    }

}