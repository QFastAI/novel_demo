package com.aiso.qfast.base.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.WindowManager
import androidx.annotation.ArrayRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aiso.qfast.base.R

class AppDialogParams {

    data class Item(
        val text: CharSequence,
        val selected: Boolean = false,
        val clickListener: (() -> Unit)? = null
    )

    class AlertParams {
        internal var title: CharSequence? = null
        internal var subTitle: CharSequence? = null
        internal var message: CharSequence? = null

        internal var positiveButtonText: CharSequence? = null
        internal var positiveButtonListener: DialogInterface.OnClickListener? = null

        internal var positiveButtonTextColor: Int? = null

        internal var negativeButtonText: CharSequence? = null
        internal var negativeButtonListener: DialogInterface.OnClickListener? = null

        internal var negativeButtonTextColor: Int? = null

        internal var cancelable = true
        internal var cancelListener: DialogInterface.OnCancelListener? = null
        internal var dismissListener: DialogInterface.OnDismissListener? = null

        internal var items: List<Item>? = null

        @LayoutRes
        internal var itemLayoutRes: Int? = null
        internal var itemClickListener: DialogInterface.OnClickListener? = null

        internal var customView: View? = null
        internal var customViewLayoutResId: Int = ResourcesCompat.ID_NULL

        internal var customViewSpace: Boolean = false
        internal var customViewSpaceLeft: Int = 0
        internal var customViewSpaceRight: Int = 0
        internal var customViewSpaceTop: Int = 0
        internal var customViewSpaceBottom: Int = 0

        // Extra
        internal var showCloseButton = false
        internal var maxTotalItemHeight: Int = 0
        internal var maxMessageHeight: Int = 0
        internal var dialogBackgroundResId: Int = R.drawable.theme_bg_dialog
    }

    abstract class AppAlertBuilder<Dialog : AppCompatDialog>(protected val context: Context?) {
        protected val alertParams = AlertParams()

        internal var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

        /**
         * 标题
         */
        fun setTitle(@StringRes titleId: Int): AppAlertBuilder<Dialog> {
            alertParams.title = context?.getText(titleId)
            return this
        }

        /**
         * 标题
         */
        fun setTitle(title: CharSequence?): AppAlertBuilder<Dialog> {
            alertParams.title = title
            return this
        }

        /**
         * 副标题
         */
        fun setSubTitle(title: CharSequence?): AppAlertBuilder<Dialog> {
            alertParams.subTitle = title
            return this
        }

        /**
         * 内容
         */
        fun setMessage(@StringRes messageId: Int): AppAlertBuilder<Dialog> {
            alertParams.message = context?.getText(messageId)
            return this
        }

        /**
         * 内容
         */
        fun setMessage(message: CharSequence?): AppAlertBuilder<Dialog> {
            alertParams.message = message
            return this
        }

        /**
         * 内容最大高度
         */
        fun maxMessageHeight(maxHeight: Int): AppAlertBuilder<Dialog> {
            alertParams.maxMessageHeight = maxHeight
            return this
        }

        /**
         * 设置dialog背景图
         */
        fun setDialogBackgroundResId(resId: Int): AppAlertBuilder<Dialog> {
            alertParams.dialogBackgroundResId = resId
            return this
        }

        /**
         * 确定按钮
         */
        fun setPositiveButton(
            @StringRes textId: Int, listener: DialogInterface.OnClickListener? = null
        ): AppAlertBuilder<Dialog> {
            alertParams.positiveButtonText = context?.getText(textId)
            alertParams.positiveButtonListener = listener
            return this
        }

        /**
         * 确定按钮
         */
        fun setPositiveButton(
            text: CharSequence, listener: DialogInterface.OnClickListener? = null
        ): AppAlertBuilder<Dialog> {
            alertParams.positiveButtonText = text
            alertParams.positiveButtonListener = listener
            return this
        }

        /**
         * 确定按钮颜色
         */
        fun setPositiveButtonColorResource(@ColorRes colorId: Int): AppAlertBuilder<Dialog> {
            if (context != null) {
                setPositiveButtonColor(ContextCompat.getColor(context, colorId))
            }
            return this
        }

        /**
         * 确定按钮颜色
         */
        fun setPositiveButtonColor(@ColorInt colorId: Int): AppAlertBuilder<Dialog> {
            alertParams.positiveButtonTextColor = colorId
            return this
        }

        /**
         * 取消按钮
         */
        fun setNegativeButton(
            @StringRes textId: Int, listener: DialogInterface.OnClickListener? = null
        ): AppAlertBuilder<Dialog> {
            alertParams.negativeButtonText = context?.getText(textId)
            alertParams.negativeButtonListener = listener
            return this
        }

        /**
         * 取消按钮
         */
        fun setNegativeButton(
            text: CharSequence, listener: DialogInterface.OnClickListener? = null
        ): AppAlertBuilder<Dialog> {
            alertParams.negativeButtonText = text
            alertParams.negativeButtonListener = listener
            return this
        }

        /**
         * 取消按钮颜色
         */
        fun setNegativeButtonColorResource(@ColorRes colorId: Int): AppAlertBuilder<Dialog> {
            if (context != null) {
                setNegativeButtonColor(ContextCompat.getColor(context, colorId))
            }
            return this
        }

        /**
         * 取消按钮颜色
         */
        fun setNegativeButtonColor(@ColorInt colorId: Int): AppAlertBuilder<Dialog> {
            alertParams.negativeButtonTextColor = colorId
            return this
        }

        /**
         * 是否可取消和点击空白处取消
         */
        fun setCancelable(cancelable: Boolean): AppAlertBuilder<Dialog> {
            alertParams.cancelable = cancelable
            return this
        }

        /**
         * 取消监听
         */
        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener): AppAlertBuilder<Dialog> {
            alertParams.cancelListener = onCancelListener
            return this
        }

        /**
         * 销毁监听
         */
        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener): AppAlertBuilder<Dialog> {
            alertParams.dismissListener = onDismissListener
            return this
        }

        /***
         * @param items 不与系统 api 一致的特殊形式 ，click 逻辑放到 Item 中
         * */
        fun setItems(
            items: List<Item>, @LayoutRes itemLayoutRes: Int? = null
        ): AppAlertBuilder<Dialog> {
            alertParams.items = items
            if (itemLayoutRes != null) {
                alertParams.itemLayoutRes = itemLayoutRes
            }
            return this
        }

        /**
         * 列表
         */
        fun setItems(
            @ArrayRes itemsId: Int,
            maxTotalItemHeight: Int? = 0,
            listener: DialogInterface.OnClickListener
        ): AppAlertBuilder<Dialog> {
            alertParams.items = context?.resources?.getTextArray(itemsId)?.map {
                return@map Item(it, false)
            }
            alertParams.maxTotalItemHeight = maxTotalItemHeight ?: 0
            alertParams.itemClickListener = listener
            return this
        }

        fun setItems(
            @ArrayRes itemsId: Int,
            selectedPosition: Int,
            maxTotalItemHeight: Int? = 0,
            listener: DialogInterface.OnClickListener
        ): AppAlertBuilder<Dialog> {
            alertParams.items = context?.resources?.getTextArray(itemsId)
                ?.mapIndexed { index, charSequence ->
                    return@mapIndexed Item(charSequence, selectedPosition == index)
                }
            alertParams.maxTotalItemHeight = maxTotalItemHeight ?: 0
            alertParams.itemClickListener = listener
            return this
        }

        fun setItems(
            items: Array<out CharSequence>,
            maxTotalItemHeight: Int? = 0,
            listener: DialogInterface.OnClickListener
        ): AppAlertBuilder<Dialog> {
            alertParams.items = items.map {
                return@map Item(it, false)
            }
            alertParams.maxTotalItemHeight = maxTotalItemHeight ?: 0
            alertParams.itemClickListener = listener
            return this
        }

        fun setItems(
            items: Array<out CharSequence>,
            selectedPosition: Int,
            maxTotalItemHeight: Int? = 0,
            listener: DialogInterface.OnClickListener
        ): AppAlertBuilder<Dialog> {
            alertParams.items = items.mapIndexed { index, charSequence ->
                return@mapIndexed Item(charSequence, selectedPosition == index)
            }
            alertParams.maxTotalItemHeight = maxTotalItemHeight ?: 0
            alertParams.itemClickListener = listener
            return this
        }

        /**
         * 自定义View
         */
        fun setView(layoutResId: Int): AppAlertBuilder<Dialog> {
            alertParams.customView = null
            alertParams.customViewLayoutResId = layoutResId
            return this
        }

        fun setView(view: View): AppAlertBuilder<Dialog> {
            alertParams.customView = view
            alertParams.customViewLayoutResId = ResourcesCompat.ID_NULL
            return this
        }

        fun setView(
            view: View,
            viewSpacingLeft: Int,
            viewSpacingTop: Int,
            viewSpacingRight: Int,
            viewSpacingBottom: Int
        ): AppAlertBuilder<Dialog> {
            alertParams.customViewSpace = true
            alertParams.customViewSpaceLeft = viewSpacingLeft
            alertParams.customViewSpaceTop = viewSpacingTop
            alertParams.customViewSpaceRight = viewSpacingRight
            alertParams.customViewSpaceBottom = viewSpacingBottom
            return setView(view)
        }

        /**
         * 是否显示关闭按钮
         */
        fun setCloseButton(show: Boolean): AppAlertBuilder<Dialog> {
            alertParams.showCloseButton = show
            return this
        }

        /**
         * 输入框模式
         */
        fun setSoftInputMode(softInputMode: Int): AppAlertBuilder<Dialog> {
            this.softInputMode = softInputMode
            return this
        }

        @Deprecated("推荐直接使用 show 方法。 create 可能返回 null")
        fun create(): Dialog? {
            return internalCreate()
        }

        abstract fun internalCreate(): Dialog?

        fun show() {
            val dialog = internalCreate()
            if (dialog != null && isContextValid(context)) {
                try {
                    dialog.show()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }

        @SuppressLint("NewApi")
        private fun isContextValid(context: Context?): Boolean {
            if (context == null) return false
            if (context is Activity) {
                if (context.isFinishing || context.isDestroyed) {
                    return false
                }
            }
            return true
        }
    }
}
