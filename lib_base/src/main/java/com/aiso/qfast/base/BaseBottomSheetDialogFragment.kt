package com.aiso.qfast.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Window
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.graphics.ColorUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import java.lang.ref.WeakReference

open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    private var dismissCount = 0
    private lateinit var reference: WeakReference<Context>
    override fun onCreate(savedInstanceState: Bundle?) {
        reference = WeakReference<Context>(requireContext())
        super.onCreate(savedInstanceState)
    }

    /**
     * 配置 Dialog
     *
     * @return Config
     */

    override fun getTheme(): Int {
        return R.style.theme_bottom_sheet_dialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return reference.get()?.let { res ->
            object : BottomSheetDialog(res, theme) {

                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    window?.let {
                        onSetupSoftInputMode(it)
                    }
                    onDialogCreate(this, savedInstanceState)
                }

                override fun show() {
                    dismissCount = 0
                    super.show()
                }

                override fun dismiss() {
                    if (dismissCount == 0) {
                        onDialogDismiss(this)
                    }
                    dismissCount++
                    super.dismiss()
                }
            }
        } ?: super.onCreateDialog(savedInstanceState)
    }

    /**
     * Dialog onCreate 时，适合配置 window 等属性
     */
    protected open fun onDialogCreate(dialog: BottomSheetDialog, savedInstanceState: Bundle?) {}

    /**
     * Dialog 触发 dismiss 时
     */
    protected open fun onDialogDismiss(dialog: BottomSheetDialog) {}

    /**
     * Dialog onCreate 时，onSetupSoftInputMode 设置软键盘属性
     */
    protected open fun onSetupSoftInputMode(window: Window) {
        // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    /**
     * 这个系统周期不精准，使用 {@see onDialogDismiss }，在 dismiss 之前处理事件
     */
    @Deprecated(
        message = "这个系统周期不精准", replaceWith = ReplaceWith("onDialogDismiss")
    )
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    fun tintBackground(@ColorInt tintColor: Int) {
        (dialog as? BottomSheetDialog)?.tintBackground(tintColor)
    }

    companion object {
        @SuppressLint("NewApi")
        fun BottomSheetDialog.tintBackground(@ColorInt tintColor: Int) {
            try {
                val shapeDrawable = behavior.javaClass.getDeclaredMethod("getMaterialShapeDrawable")
                    .let {
                        it.isAccessible = true
                        it.invoke(behavior)
                    }
                (shapeDrawable as? MaterialShapeDrawable)?.setTint(tintColor)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            window?.navigationBarColor = ColorUtils.setAlphaComponent(tintColor, 0xCC)
        }
    }
}