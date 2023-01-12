package com.example.movieindex.core.common

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.TypedValue
import androidx.recyclerview.widget.DiffUtil
import com.amulyakhare.textdrawable.TextDrawable
import com.example.movieindex.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random.Default.nextInt


fun addAlpha(alpha: Float, color: Int) =
    Color.argb((255 * alpha).toInt(), Color.red(color), Color.green(color),
        Color.blue(color))

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}

fun pxToDp(px: Int, context: Context): Int {
    return (px / context.resources.displayMetrics.density).toInt()
}

fun spToPx(sp: Float, context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
        sp,
        context.resources.displayMetrics).toInt()
}

fun dpToSp(dp: Float, context: Context): Int {
    return (dpToPx(dp, context) / context.resources.displayMetrics.scaledDensity).toInt()
}

fun getCurrentLocale(context: Context): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }
}

fun getShimmerDrawable(): ShimmerDrawable {
    val shimmer = Shimmer.AlphaHighlightBuilder().setDuration(1000)
        .setBaseAlpha(0.7F)
        .setHighlightAlpha(0.6F).setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    return ShimmerDrawable().apply {
        setShimmer(shimmer)
    }
}

fun getCircleTextDrawable(text: String, color: Int, fontSize: Int) =
    TextDrawable.builder().beginConfig().fontSize(fontSize).bold().endConfig()
        .buildRound(text = text,
            color = color)

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

class RvDiffUtil<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

fun timeConverter(time: String, locale: Locale): String? {
    // YYYY-MM-DDTHH:MM:SSZ

    val instant = Instant.parse(time)
    instant.truncatedTo(ChronoUnit.MINUTES)
    val zoneId: ZoneId = ZoneId.systemDefault()
    val zdt = instant.atZone(zoneId)
    val localDateTime = zdt.toLocalDateTime()
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale)
    return localDateTime.format(formatter)
}

fun getRandomColor(): Int {
    return Color.rgb(nextInt(255), nextInt(255), nextInt(255))
}

fun colorChanger(color: Int, factor: Float): Int {
    val a = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255))
}

fun getMovieRatingIndicatorColor(movieRating: Int) = if (movieRating > 50) {
    R.color.ratings_green
} else if (movieRating in 26..50) {
    R.color.ratings_yellow
} else {
    R.color.ratings_red
}

fun getMovieRatingTrackColor(movieRating: Int) = if (movieRating > 50) {
    R.color.ratings_green_track_color
} else if (movieRating in 26..50) {
    R.color.ratings_yellow_track_color
} else {
    R.color.ratings_red_track_color
}