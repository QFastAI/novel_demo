package com.aiso.qfast.utils.glide

import android.graphics.Color
import android.net.Uri
import androidx.fragment.app.Fragment
import com.luck.picture.lib.engine.CropFileEngine
import com.yalantis.ucrop.UCrop

class ImageAvatarCropEngine(
    private var isCircleDimmedLayer: Boolean = false,
    private var aspectRatioX: Int? = null,
    private var aspectRatioY: Int? = null,

) : CropFileEngine {
    override fun onStartCrop(
        fragment: Fragment,
        srcUri: Uri,
        destinationUri: Uri,
        dataSource: ArrayList<String>?,
        requestCode: Int,
    ) {
        val options: UCrop.Options = buildOptions(aspectRatioX?:1, aspectRatioY?:1, isCircleDimmedLayer)
        val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
        uCrop.withOptions(options)
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }

    private fun buildOptions(aspectRatioX: Int = 0, aspectRatioY: Int = 0,isCircleDimmedLayer: Boolean = false ): UCrop.Options {
        val options = UCrop.Options()
        options.setToolbarTitle("")
        //options.color
        options.setHideBottomControls(true)
        //options.setFreeStyleCropEnabled(freeStyleCropEnabled)
        options.setShowCropFrame(false)
        options.setShowCropGrid(false)
        options.setCircleDimmedLayer(isCircleDimmedLayer)
        options.withAspectRatio(aspectRatioX.toFloat(), aspectRatioY.toFloat())
        //options.setCropOutputPathDir(getSandboxPath())
        options.isCropDragSmoothToCenter(false)
        //options.setSkipCropMimeType(*getNotSupportCrop())
        //options.isForbidCropGifWebp(cb_not_gif.isChecked())
        options.isForbidSkipMultipleCrop(true)
        options.setMaxScaleMultiplier(100f)
        //
        options.setStatusBarColor(
            Color.WHITE
        )
        options.setToolbarColor(Color.WHITE)
        options.isDarkStatusBarBlack(true)
        //options.setToolbarWidgetColor(
        //    ContextCompat.getColor(
        //        MBUtil.getContext(), R.color.ps_color_grey
        //    )
        //)
        return options
    }
}