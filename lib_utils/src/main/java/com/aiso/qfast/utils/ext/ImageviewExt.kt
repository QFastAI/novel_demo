package com.aiso.qfast.utils.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.aiso.qfast.utils.ImageLoader
import com.aiso.qfast.utils.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import timber.log.Timber

/**
 * @date: 2024/1/23
 * desc:
 * author: zhaolei
 * version:
 */
fun ImageView.loadNetBitmap(context: Context, url: String, defaultErrorRes: Int) {

    val options = RequestOptions().transform(CenterCrop())
    Glide.with(context)
        .load(ImageLoader.formatUrl(url, width, height))
        .apply(options)
        .placeholder(defaultErrorRes)//图片加载出来前，显示的图片
        .fallback(defaultErrorRes) //url为空的时候,显示的图片
        .error(defaultErrorRes)//图片加载失败后，显示的图片
        .into(this)
}

fun ImageView.loadNetBitmapAndListener(
    imageView: ImageView,
    // 加载内容
    media: Any,
    resId: Int = 0,
    success: () -> Unit = {},
    fail: () -> Unit = {},
) {
    val options = RequestOptions()
    Glide.with(imageView.context).load(media)
        .diskCacheStrategy(DiskCacheStrategy.ALL) // 开启缓存
        .placeholder(resId)
        .error(resId)
        .apply(options)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                Timber.e("glide加载图片错误${e}")
                fail()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                success()
                return false
            }
        })
        .into(imageView).clearOnDetach()
}

/**
 * 加载网络图片
 * @param url 图片地址
 * https://example-1258125638.cos.ap-shanghai.myqcloud.com/sample.png?imageMogr2/format/webp/cut/600x600
 */
fun ImageView.loadUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        return
    }
    Glide.with(context).load(ImageLoader.formatUrl(url, width, height)).into(this)
}

fun ImageView.loadNetBitmap(
    url: String,
    defaultErrorRes: Int = R.drawable.shape_placeholder
) {

    val options = RequestOptions().transform(CenterCrop())
    Glide.with(context)
        .load(ImageLoader.formatUrl(url, width, height))
        .apply(options)
        .placeholder(defaultErrorRes)//图片加载出来前，显示的图片
        .fallback(defaultErrorRes) //url为空的时候,显示的图片
        .error(defaultErrorRes)//图片加载失败后，显示的图片
        .into(this)
}

fun ImageView.loadNetBitmap(url: String, placeholder: Int, error: Int) {
    val options = RequestOptions().transform(CenterCrop())
    Glide.with(context)
        .load(ImageLoader.formatUrl(url, width, height))
        .apply(options)
        .placeholder(placeholder)//图片加载出来前，显示的图片
        .fallback(placeholder) //url为空的时候,显示的图片
        .error(error)//图片加载失败后，显示的图片
        .into(this)
}

fun ImageView.loadVideoHolderBitmap(context: Context, url: String, defaultErrorRes: Int) {
    loadVideoHolderBitmap(context, url, defaultErrorRes, false)
}

fun ImageView.loadVideoHolderBitmap(
    context: Context,
    url: String,
    defaultErrorRes: Int,
    isCenterCrop: Boolean
) {
    var requestOptions = RequestOptions()
    requestOptions = if (isCenterCrop) {
        requestOptions.centerCrop()
    } else {
        requestOptions.centerInside()
    }
    Glide.with(context)
        .load(ImageLoader.formatUrl(url, width, height))
        .skipMemoryCache(true)
        .format(DecodeFormat.PREFER_RGB_565)
        .apply(requestOptions)
        .placeholder(defaultErrorRes)
        .transition(DrawableTransitionOptions.withCrossFade(300))
        .error(defaultErrorRes)
        .into(this)
}

/**
 * 清掉内存缓存
 */
@SuppressLint("NewApi")
fun ImageView.clear() {
    if (context is Activity && ((context as Activity).isFinishing || (context as Activity).isDestroyed)) {
        // 避免在 Activity 销毁时调用 Glide，防止崩溃
        return
    }
    Glide.with(context).clear(this)
    setImageDrawable(null)
}

