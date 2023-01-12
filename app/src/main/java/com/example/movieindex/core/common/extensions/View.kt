package com.example.movieindex.core.common.extensions

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.SuperscriptSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.movieindex.R
import com.example.movieindex.core.common.getShimmerDrawable
import timber.log.Timber


fun TextView.textWithColorClickable(
    initialMsg: String,
    highlightString: String,
    @ColorRes colorRes: Int,
    navigationAction: () -> Unit,
) {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(p0: View) {
            navigationAction()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ContextCompat.getColor(context, colorRes)
            ds.isUnderlineText = true
        }
    }

    val fullText = "$initialMsg $highlightString"
    text = fullText

    val startIndex = fullText.indexOf(highlightString)
    val endIndex = startIndex + highlightString.length

    SpannableString(fullText).apply {
        setSpan(
            clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = this
    }

    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.removeSpan() {
    if (text is SpannableString) {
        val spannableStr = text as SpannableString
        val spans = spannableStr.getSpans(0, spannableStr.length, ClickableSpan::class.java)
        for (span in spans) {
            spannableStr.removeSpan(span)
        }
        text = spannableStr
    }
}

fun TextView.toSuperScript(startIndex: Int, endIndex: Int) {
    val superscriptSpan = SuperscriptSpan()
    val builder = SpannableStringBuilder(text)
    builder.setSpan(
        superscriptSpan,
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = builder
}

fun TextView.setColor(@ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(this.context, colorRes)
    setTextColor(color)
}

fun ImageView.loadCircleImage(
    source: String?,
    placeholder: Drawable? = null,
    error: Drawable? = null,
    onLoadFinish: (resource: Drawable?) -> Unit = {},
    onLoadFailed: () -> Unit = {},
) {
    Glide.with(this.context)
        .load(source)
        .placeholder(placeholder ?: getShimmerDrawable())
        .error(error ?: ContextCompat.getDrawable(this.context, R.drawable.ic_error_placeholder))
        .circleCrop()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                Timber.i("glide logger - errMsg: ${e?.message}")
                onLoadFailed()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                onLoadFinish(resource)
                return false
            }
        })
        .into(this)
}

fun ImageView.loadErrorImage() {
    Glide.with(this.context)
        .load(ContextCompat.getDrawable(this.context, R.drawable.ic_error_placeholder)).into(this)
}

fun ImageView.loadImage(
    source: String?,
    placeholder: Drawable? = null,
    @DrawableRes errorPlaceholderRes: Int = R.drawable.ic_error_placeholder,
    onLoadFinish: (resource: Drawable?) -> Unit = {},
    onLoadFailed: () -> Unit = {},
) {
    Glide.with(this.context)
        .load(source)
        .placeholder(placeholder ?: getShimmerDrawable())
        .error(errorPlaceholderRes)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                Timber.i("glide logger - errMsg: ${e?.message}")
                onLoadFailed()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                onLoadFinish(resource)
                return false
            }
        })
        .into(this)
}

fun ImageView.loadImageRoundedCorner(
    source: String?,
    placeholder: Drawable? = null,
    @DrawableRes errorPlaceholderRes: Int = R.drawable.ic_error_placeholder,
    onLoadFinish: (resource: Drawable?) -> Unit = {},
    onLoadFailed: () -> Unit = {},
    radius: Int,
) {
    Glide.with(this.context)
        .load(source)
        .transform(RoundedCorners(radius))
        .placeholder(placeholder ?: getShimmerDrawable())
        .error(errorPlaceholderRes)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                Timber.i("glide logger - errMsg: ${e?.message}")
                onLoadFailed()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                onLoadFinish(resource)
                return false
            }
        })
        .into(this)
}