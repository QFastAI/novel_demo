package com.aiso.qfast.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.aiso.qfast.utils.glide.CornerTransform
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.luck.picture.lib.photoview.PhotoView
import com.luck.picture.lib.utils.ActivityCompatHelper
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.roundToInt

/**
 * @date：2023/11/13 14:16
 * desc: 图片加载引擎
 * author: sunyuxin
 * version：1.0
 **/
object ImageLoader {
    private fun loadImage(
        activity: FragmentActivity,
        imageUrl: String,
        imageView: ImageView,
        options: RequestOptions,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(activity)) {
            return
        }
        Glide.with(activity).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).apply(options)
            .into(imageView)
    }

    private fun loadImageSkipMemory(
        activity: FragmentActivity,
        imageUrl: String,
        imageView: ImageView,
        options: RequestOptions,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(activity)) {
            return
        }
        Glide.with(activity).load(formatUrl(imageUrl, imageView.width, imageView.height))
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL).apply(options)
            .into(imageView)
    }

    /**
     * 默认加载
     */
    fun loadImageDefault(
        activity: FragmentActivity,
        imageUrl: String,
        imageView: ImageView,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(activity)) {
            return
        }
        Glide.with(activity).load(formatUrl(imageUrl, imageView.width, imageView.height))
            .into(imageView)
    }


    /**
     * 默认加载
     */
    fun loadImageWithOutCache(
        activity: FragmentActivity,
        imageUrl: String,
        imageView: ImageView,
        defaultImageId: Int,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(activity)) {
            return
        }
        Glide.with(activity).load(formatUrl(imageUrl, imageView.width, imageView.height))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(defaultImageId)
            .skipMemoryCache(true).into(imageView)
    }

    /**
     * 加载头像
     */
    fun loadAvatar(
        imageView: ImageView, media: Any, resId: Int = 0,
        success: () -> Unit = {},
        fail: () -> Unit = {},
    ) {
        loadCover(
            imageView,
            media,
            radius = 8f,
            resId = resId,
            isThumbnail = true,
            success = success,
            fail = fail
        )
    }

    /**
     * 加载圆型头像
     */
    fun loadCircleAvatar(
        imageView: ImageView, media: Any, resId: Int = 0,
        success: () -> Unit = {},
        fail: () -> Unit = {},
    ) {
        loadCover(
            imageView,
            media,
            isCircle = true,
            radius = 0f,
            resId = resId,
            isThumbnail = true,
            success = success,
            fail = fail
        )
    }


    /**
     * 加载表情图片
     */
    @SuppressLint("CheckResult")
    fun loadEmote(
        imageView: ImageView,
        // 加载内容
        media: Any,
        resId: Int = 0,
        success: (resource: Drawable?) -> Unit = {},
        fail: () -> Unit = {},
    ) {
        Glide.with(imageView.context).asDrawable().load(media)
            .error(resId)
            .placeholder(resId)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // 开启缓存
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    fail()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    success(resource)
                    return false
                }
            })
//            .into(imageView)
    }

    fun loadMarkerImageForResource(
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
                    imageView.setImageDrawable(resource)
                    success()
                    return false
                }
            })
            .into(imageView).clearOnDetach()
    }

    /**
     * 加载封面图片
     */
    fun loadCover(
        imageView: ImageView,
        // 加载内容
        media: Any,
        // 圆形
        isCircle: Boolean = false,
        // 圆角
        radius: Float = 0f,
        // 四角分别设置圆角
        radiusTL: Float = 0f,
        radiusTR: Float = 0f,
        radiusBR: Float = 0f,
        radiusBL: Float = 0f,
        resId: Int = 0,
        // 缩略图，这个一般用在加载头像图片上，可以节省资源
        isThumbnail: Boolean = false,
        thumbnailSize: Int = 192,
        isVideo: Boolean = false, // 是否为视频文件，是的话就需要获取第一帧
        isBlur: Boolean = false, // 高斯模糊
        isBlurPixel: Boolean = false, // 像素模糊
        success: () -> Unit = {},
        fail: () -> Unit = {},
    ) {
        val options = RequestOptions()
        val transformations: MutableList<Transformation<Bitmap>> = mutableListOf()
        if (isCircle) {
            // 圆形
            options.optionalCircleCrop()
        } else {
            if (radius > 0) {
                // 统一设置圆角
                transformations.add(CenterCrop())
                transformations.add(GranularRoundedCorners(radius, radius, radius, radius))
            } else if (radiusTL > 0 || radiusTR > 0 || radiusBL > 0 || radiusBR > 0) {
                // 四角单独设置
                transformations.add(CenterCrop())
                transformations.add(GranularRoundedCorners(radiusTL, radiusTR, radiusBR, radiusBL))
            }
        }
        if (isThumbnail) {
            // 只加载指定大小的缩略图
            options.format(DecodeFormat.PREFER_RGB_565).override(thumbnailSize)
        }
        // 模糊变换
        if (isBlur) {
            transformations.add(0, BlurTransformation(25, 8))
        }
        // 像素模糊变换
        if (isBlurPixel) {
            transformations.add(0, PixelationFilterTransformation(20f))
        }
        if (transformations.isNotEmpty()) {
            options.transform(MultiTransformation(transformations))
        }

        if (!ActivityCompatHelper.assertValidRequest(imageView.context)) {
            return
        }

        // 视频封面需要获取第一帧来实现
        if (isVideo) {
            options.frame(1)
        }
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
     * 加载带有圆角的网络图片
     * 默认没有
     */
    fun loadImage(
        activity: FragmentActivity,
        imageUrl: String,
        imageView: ImageView,
        radius: Int? = 0,
        skipMemoryCache: Boolean = false,
    ) {
        val options =
            RequestOptions().transform(MultiTransformation(radius?.let { RoundedCorners(it) })) // 设置圆角，参数是圆角的半径
        if (skipMemoryCache) {
            loadImageSkipMemory(activity, imageUrl, imageView, options)
        } else {
            loadImage(activity, imageUrl, imageView, options)
        }
    }

    fun loadImageTopCorner(
        activity: FragmentActivity,
        imageUrl: String,
        imageView: ImageView,
        radius: Int? = 0,
    ) {
        val cornerTransform =
            radius?.toFloat()?.let { DensityUtils.dpToPx(activity, it).toFloat() }?.let {
                CornerTransform(
                    activity,
                    it
                )
            };
        cornerTransform?.setNeedCorner(true, true, false, false);
        val options =
            RequestOptions().transform(cornerTransform) // 设置圆角，参数是圆角的半径

        loadImage(activity, imageUrl, imageView, options)
    }

    fun loadCircleImage(
        context: Context,
        imageUrl: String?,
        imageView: ImageView,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        val options = RequestOptions().optionalCircleCrop() // 设置圆角，参数是圆角的半径
        Glide.with(context).load(formatUrl(imageUrl, imageView.width, imageView.height))
            .placeholder(R.drawable.shape_placeholder)
            .error(R.drawable.shape_placeholder).apply(options).into(imageView)
    }

    fun syncGetBitmap(context: Context, imageUrl: String?, radius: Int = 0): Bitmap? {
        if (!ActivityCompatHelper.assertValidRequest(context)) return null
        return runCatching {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .transform(MultiTransformation(RoundedCorners(radius)))
                .submit()
                .get()
        }.getOrNull()
    }

    fun calcScaleImageSize(
        originWidth: Double,
        originHeight: Double,
        defaultMaxSize: Int = 1080
    ): Pair<Int, Int> {
        var defaultWidth = defaultMaxSize
        var defaultHeight = defaultMaxSize
        if (originWidth > 0 && originHeight > 0) {
            if (originWidth > originHeight && originWidth > defaultMaxSize) {
                val factor = originWidth / defaultMaxSize
                defaultHeight = (originHeight / factor).toInt()
            } else if (originHeight > originWidth && originHeight > defaultMaxSize) {
                val factor = originHeight / defaultMaxSize
                defaultWidth = (originWidth / factor).toInt()
            } else if (originWidth <= defaultMaxSize && originHeight <= defaultMaxSize) {
                defaultWidth = originWidth.roundToInt().coerceAtMost(defaultWidth)
                defaultHeight = originHeight.roundToInt().coerceAtMost(defaultHeight)
            }
        }
        return Pair(defaultWidth, defaultHeight)
    }

    /**
     * glide加载本地的图片
     * 也可以设置圆角 默认没有
     */
    fun loadLocalImage(
        activity: FragmentActivity,
        filePath: String,
        imageView: ImageView,
        radius: Int?,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(activity)) {
            return
        }
        val options =
            RequestOptions().transform(MultiTransformation(radius?.let { RoundedCorners(it) })) //
        Glide.with(activity).load(File(filePath)) // 将文件路径转换为 File 对象
            .apply(options).into(imageView)
    }

    fun ImageView.loadImage(url: String?, isCircle: Boolean = false) {

        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        Glide.with(this.context).load(url)
            .apply {
                if (isCircle) {
                    optionalCircleCrop()
                }
            }.into(this)
    }

    fun ImageView.loadImage(resId: Int, isCircle: Boolean = false) {

        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        Glide.with(this.context).load(resId).format(DecodeFormat.PREFER_RGB_565).apply {
            if (isCircle) {
                optionalCircleCrop()
            }
        }.into(this)
    }

    fun ImageView.loadImageNoCache(resId: Int, isCircle: Boolean = false) {

        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        Glide.with(this.context).load(resId).skipMemoryCache(true)
            .format(DecodeFormat.PREFER_RGB_565).apply {
                if (isCircle) {
                    optionalCircleCrop()
                }
            }.into(this)
    }

    fun ImageView.loadImageWithDefault(url: String?, defaultErrorRes: Int = com.aiso.qfast.base.R.color.white) {
        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        Glide.with(this.context).load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(defaultErrorRes)//图片加载出来前，显示的图片
            .fallback(defaultErrorRes) //url为空的时候,显示的图片
            .error(defaultErrorRes)//图片加载失败后，显示的图片
            .into(this)
    }

    fun ImageView.loadImage(url: String, placeholder: Int, isCircle: Boolean = false) {

        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        Glide.with(this.context).load(url).apply {
            if (isCircle) {
                optionalCircleCrop()
            }
        }.placeholder(placeholder).into(this)
    }

    fun ImageView.loadImage(
        url: String,
        topleft: Float,
        topright: Float,
        bottomright: Float,
        bottomleft: Float,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        val options = RequestOptions().transform(
            MultiTransformation(
                GranularRoundedCorners(
                    topleft, topright, bottomright, bottomleft
                )
            )
        ) //

        Glide.with(this.context).load(url).apply(options).into(this)
    }

    fun ImageView.loadImage(url: String, corners: Int) {
        if (!ActivityCompatHelper.assertValidRequest(this.context)) {
            return
        }
        Glide.with(this.context).load(url)
            .transform(CenterCrop(), RoundedCorners(corners))
            .into(this)
    }

    fun loadMsgImage(
        iv: ImageView,
        url: String,
        topleft: Float,
        topright: Float,
        bottomright: Float,
        bottomleft: Float,
        success: () -> Unit,
        fail: () -> Unit,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(iv.context)) {
            return
        }

        val options = RequestOptions().transform(
            MultiTransformation(
                CenterCrop(),
                GranularRoundedCorners(
                    topleft, topright, bottomright, bottomleft
                )
            )
        ) //
        Glide.with(iv.context).load(formatUrl(url, iv.width, iv.height))
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
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
            }).into(iv)
    }


//    fun assertValidRequest(context: Context?): Boolean {
//        if (context is Activity) {
//            return !ActivityCompatHelper.isDestroy(context)
//        } else if (context is ContextWrapper) {
//            val contextWrapper = context
//            if (context.baseContext is Activity) {
//                val activity = contextWrapper.baseContext as Activity
//                return !ActivityCompatHelper.isDestroy(activity)
//            }
//        }
//        return true
//    }

    @SuppressLint("NewApi")
    private fun isDestroy(activity: Activity?): Boolean {
        return if (activity == null) {
            true
        } else activity.isFinishing || activity.isDestroyed
    }

    /**
     * 获取glide的缓存大小
     */
    fun Context.getGlideCacheSize(): Long {
        val glideCacheDir = Glide.getPhotoCacheDir(this)
        val cacheFiles = glideCacheDir?.listFiles()
        var cacheSize: Long = 0

        cacheFiles?.forEach { file ->
            cacheSize += file.length()
        }
        return cacheSize
    }

    fun Context.clearGlideCache() {
        Glide.get(this).clearDiskCache()
    }


    //消息原图
    fun loadMsgOriginalImage(
        coverImageView: PhotoView,
        url: String,
    ) {

        Glide.with(coverImageView.context)
            .asBitmap()
            .load(formatUrl(url, coverImageView.width, coverImageView.height))
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    bitmap: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    // 在这里处理加载完成的图片，例如将其设置为 ImageView 的背景
                    coverImageView.setImageBitmap(bitmap)
                }
            })

    }

    fun ImageView.loadCircularBitmap(bitmap: Bitmap) {
        Glide.with(this.context).load(bitmap).apply(RequestOptions.bitmapTransform(CircleCrop()))
            .into(
                this
            )
    }

    //消息列表和阅后即焚
    @SuppressLint("CheckResult")
    fun loadBrunAfterReadMsgImage(
        iv: ImageView,
        url: String,
        topleft: Float,
        topright: Float,
        bottomright: Float,
        bottomleft: Float,
        success: () -> Unit,
        fail: () -> Unit,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(iv.context)) {
            return
        }
        val options = RequestOptions().transform(
            PixelationFilterTransformation(20f),
            CenterCrop(),
            GranularRoundedCorners(
                topleft, topright, bottomright, bottomleft
            )
        )

        Glide.with(iv.context).load(formatUrl(url, iv.width, iv.height)).apply(options)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
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
            }).into(iv)
    }

    /**
     * 下载原图到缓存
     */
    fun downloadOriginImageToCache(
        context: Context,
        url: String,
        cachePath: String,
        callback: (Int, Int) -> Unit = { code, progress -> }
    ) {
        Glide.with(context).downloadOnly().load(url).listener(object : RequestListener<File> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<File>?,
                isFirstResource: Boolean
            ): Boolean {
                callback.invoke(-1, 0)
                return false
            }

            override fun onResourceReady(
                resource: File?,
                model: Any?,
                target: Target<File>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                val inputStream: InputStream = FileInputStream(resource)
                //输出流
                var outputStream: OutputStream = FileOutputStream(cachePath)
                var currentLength = 0
                val totalLength = resource?.length() ?: 1
                try {
                    val buff = ByteArray(4096)
                    var len = 0
                    while (inputStream.read(buff).also { len = it } != -1) {
                        outputStream.write(buff, 0, len)
                        currentLength += len
                        // 计算当前下载百分比，并经由回调传出
                        val progress = (100 * currentLength / totalLength).toInt()
//                        Timber.i("progress $progress")
                        callback.invoke(0, progress)
                    }
                    outputStream.flush()
                    callback.invoke(1, 100)
                } catch (e: FileNotFoundException) {
//                    Timber.e("copyFileToMedia error: ${e.message}")
                    callback.invoke(-1, 0)
                } catch (e: IOException) {
//                    Timber.e("copyFileToMedia error: ${e.message}")
                    callback.invoke(-1, 0)
                } finally {
                    try {
                        outputStream.close() // 关闭输出流
                    } catch (e: IOException) {
//                        Timber.e("copyFileToMedia error: ${e.message}")
                        callback.invoke(-1, 0)
                    }
                    try {
                        inputStream.close() // 关闭输入流
                    } catch (e: IOException) {
//                        Timber.e("copyFileToMedia error: ${e.message}")
                        callback.invoke(-1, 0)
                    }
                }
                return true
            }
        }).submit()
    }

    /**
     * 保存图片到相册
     */
    fun downloadImageToGallery(
        context: Context,
        imageUrl: String,
        fileName: String,
        callBack: SaveCallBackListener? = null
    ) {
        Glide.with(context).downloadOnly().load(imageUrl).listener(object : RequestListener<File> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<File>?,
                isFirstResource: Boolean
            ): Boolean {
                callBack?.onSuccess(false, "保存失败")
//                ToastUtils.showToast("保存失败请重试")
                return false
            }

            @SuppressLint("NewApi")
            override fun onResourceReady(
                resource: File?,
                model: Any?,
                target: Target<File>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                resource?.let {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    // 获取 ContentResolver 实例
                    val contentResolver: ContentResolver = context.contentResolver

                    // 插入图片到 MediaStore
                    val imageUri: Uri? = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    imageUri?.let { uri ->
                        try {
                            contentResolver.openOutputStream(uri).use { out ->
                                // 将文件内容写入到 MediaStore 中
                                val inputStream = FileInputStream(resource)
                                val buffer = ByteArray(1024)
                                var length: Int
                                while (inputStream.read(buffer).also { length = it } > 0) {
                                    out?.write(buffer, 0, length)
                                }
                                // 刷新并关闭流
                                out?.flush()
                                MediaScannerConnection.scanFile(
                                    context,
                                    arrayOf(resource.absolutePath),
                                    null,
                                    null
                                )
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            // 处理写入失败的情况
                        }
                    }
                    callBack?.onSuccess(true, "已保存到相册")
//                    ToastUtils.showToast("已保存到相册")
                }
                return true
            }
        }).submit()
    }

    @SuppressLint("NewApi")
    fun saveBase64ToGallery(
        context: Context,
        base64Data: String,
        fileName: String,
        callBack: SaveCallBackListener? = null
    ) {
        try {
            // 将 Base64 字符串转换为字节数组
            val decodedBytes = Base64.decode(base64Data, Base64.DEFAULT)

            // 将字节数组转换为 Bitmap
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            bitmap?.let {
                // 创建 ContentValues 来保存图片的元数据
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName) // 图片文件名
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // 图片 MIME 类型
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES
                    ) // 存储目录
                }

                // 获取 ContentResolver 实例
                val contentResolver: ContentResolver = context.contentResolver

                // 插入图片到 MediaStore
                val imageUri: Uri? = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                imageUri?.let { uri ->
                    try {
                        // 打开输出流写入图片
                        contentResolver.openOutputStream(uri).use { out ->
                            // 将 Bitmap 写入到输出流中
                            it.compress(Bitmap.CompressFormat.JPEG, 100, out!!)
                            out?.flush()

                            // 刷新媒体库，确保图库能够及时识别到新图片
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(uri.path),
                                null,
                                null
                            )
                        }
                        callBack?.onSuccess(true, "已保存到相册")
//                        ToastUtils.showToast("已保存到相册")
                    } catch (e: IOException) {
                        e.printStackTrace()
                        callBack?.onSuccess(false, e.message.toString())
//                        ToastUtils.showToast("保存失败，请重试")
                    }
                }
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            callBack?.onSuccess(false, e.message.toString())
//            ToastUtils.showToast("无效的 Base64 数据")
        }
    }

    interface SaveCallBackListener {
        fun onSuccess(isSuccess: Boolean, msg: String)
    }

    /**
     * 在线图片地址转为webp和获取对应大小图片
     * 0x0返回全图
     */
    fun formatUrl(url: String?, width: Int, height: Int): String {
        if (url.isNullOrEmpty()) {
            return ""
        }
        if (url.contains("http").not() && url.contains("https:").not()) {
            return url
        }
        val urlBuilder = StringBuilder(url)
        if (url.contains("imageMogr2")) {
            if (url.contains("format/webp").not()) {
                urlBuilder.append("/format/webp")
            }
            if (url.contains("crop").not()) {
                urlBuilder.append("/crop/${width}x${height}/gravity/center")
            }
        } else {
            urlBuilder.append("?imageMogr2/format/webp/crop/${width}x${height}/gravity/center")
        }
        return urlBuilder.toString()
    }

}