package com.aiso.qfast.base.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aiso.qfast.base.R
import com.aiso.qfast.base.button.SkyStateButton
import java.lang.ref.WeakReference

class AppBottomAlertController(
    private val context: Context, private val dialog: AppCompatDialog, private val window: Window
) {
    private val buttonHandler = ButtonHandler(dialog)

    private lateinit var alertParams: AppBottomDialogParams.AlertParams

    private lateinit var titleView: TextView
    private lateinit var subTitleView: TextView
    private var closeView: View? = null
    private lateinit var messageLayout: View
    private lateinit var messageView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var customLayout: FrameLayout

    private lateinit var bottomGuideLine: View
    private lateinit var positiveButton: SkyStateButton
    private lateinit var negativeButton: TextView

    init {
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    internal fun setupView() {
        titleView = window.findViewById(R.id.app_dialog_title_view)
        subTitleView = window.findViewById(R.id.app_dialog_sub_title_view)
        closeView = window.findViewById(R.id.app_dialog_close_view)
        messageLayout = window.findViewById(R.id.app_dialog_message_layout)
        messageView = window.findViewById(R.id.app_dialog_message_view)
        recyclerView = window.findViewById(R.id.app_dialog_recycler_view)
        customLayout = window.findViewById(R.id.app_dialog_custom_layout)

        positiveButton = window.findViewById(R.id.app_dialog_button_positive)
        negativeButton = window.findViewById(R.id.app_dialog_button_negative)
        bottomGuideLine = window.findViewById(R.id.app_dialog_bottom_guide_line)

        setupContent()
        setupItems()
        setupCustomView()
        setupButton()
    }

    private fun setupContent() {
        if (alertParams.showCloseButton) {
            closeView?.visibility = View.VISIBLE
            closeView?.setOnClickListener { dialog.cancel() }
        } else {
            closeView?.visibility = View.GONE
        }

        if (!alertParams.title.isNullOrEmpty()) {
            titleView.visibility = View.VISIBLE
            titleView.text = alertParams.title

            if (alertParams.message.isNullOrEmpty() && alertParams.items.isNullOrEmpty() && alertParams.customView == null && alertParams.customViewLayoutResId == ResourcesCompat.ID_NULL) {
                val layoutParams = titleView.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomMargin = getDimenPx(context, R.dimen.space_20)
                layoutParams.bottomToTop = R.id.app_dialog_bottom_guide_line
                titleView.requestLayout()
            }
        } else {
            titleView.visibility = View.GONE
        }

        if (!alertParams.subTitle.isNullOrEmpty()) {
            subTitleView.visibility = View.VISIBLE
            subTitleView.text = alertParams.subTitle
        } else {
            subTitleView.visibility = View.GONE
        }

        if (!alertParams.message.isNullOrEmpty()) {
            messageLayout.visibility = View.VISIBLE
            messageView.text = alertParams.message
            if (alertParams.maxMessageHeight > 0) {
                messageLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    matchConstraintMaxHeight = alertParams.maxMessageHeight
                }
            }
        } else {
            messageLayout.visibility = View.GONE
        }
    }

    private fun setupItems() {
        val arrays = alertParams.items
        if (arrays.isNullOrEmpty() || !alertParams.message.isNullOrEmpty()) {
            recyclerView.visibility = View.GONE
            return
        }
        var itemLayoutRes = alertParams.itemLayoutRes
        if (itemLayoutRes == null) {
            itemLayoutRes = R.layout.theme_bottom_dialog_list_item_layout
        }
        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ItemAdapter(
            dialog,
            arrays,
            itemLayoutRes,
            alertParams.itemsVisibleDivider,
            alertParams.itemClickListener
        )
        val itemHeight = getDimenPx(context, R.dimen.theme_bottom_sheet_item_height)
        val totalHeight = arrays.size * itemHeight
        if (alertParams.maxTotalItemHeight in 1 until totalHeight) {
            recyclerView.layoutParams.height = alertParams.maxTotalItemHeight
            val position = findCurrentSelectedPosition(arrays)
            if (position >= 0) {
                recyclerView.scrollToPosition(position)
            }
        }
    }

    private fun findCurrentSelectedPosition(list: List<AppBottomDialogParams.Item>): Int {
        var position = -1
        for (indexed in list.withIndex()) {
            if (indexed.value.selected) {
                position = indexed.index
                break
            }
        }
        return position
    }

    private fun setupCustomView() {
        var customView: View? = null
        val customViewRedID = alertParams.customViewLayoutResId
        if (alertParams.customView != null) {
            customView = alertParams.customView
        } else if (customViewRedID != ResourcesCompat.ID_NULL) {
            customView = LayoutInflater.from(context).inflate(customViewRedID, customLayout, false)
        }

        if (customView != null) {
            customLayout.addView(
                customView, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
            if (alertParams.customViewSpace) {
                customLayout.setPadding(
                    alertParams.customViewSpaceLeft,
                    alertParams.customViewSpaceTop,
                    alertParams.customViewSpaceRight,
                    alertParams.customViewSpaceBottom
                )
            }
            customLayout.visibility = View.VISIBLE
        } else {
            customLayout.visibility = View.GONE
        }
    }

    fun canTextInput(): Boolean {
        if (::customLayout.isInitialized && customLayout.isVisible) {
            return canTextInput(customLayout)
        }
        return false
    }

    private fun setupButton() {
        positiveButton.setOnClickListener {
            bindButtonClick(DialogInterface.BUTTON_POSITIVE, alertParams.positiveButtonListener)
        }
        negativeButton.setOnClickListener {
            bindButtonClick(DialogInterface.BUTTON_NEGATIVE, alertParams.negativeButtonListener)
        }

        val showPositiveButton = !alertParams.positiveButtonText.isNullOrEmpty()
        val showNegativeButton = !alertParams.negativeButtonText.isNullOrEmpty()

        when {
            showPositiveButton && showNegativeButton -> {
                bottomGuideLine.visibility = View.VISIBLE
                positiveButton.visibility = View.VISIBLE
                negativeButton.visibility = View.VISIBLE
                positiveButton.text = alertParams.positiveButtonText
                negativeButton.text = alertParams.negativeButtonText
            }

            showPositiveButton -> {
                bottomGuideLine.visibility = View.VISIBLE
                positiveButton.visibility = View.VISIBLE
                negativeButton.visibility = View.GONE
                positiveButton.text = alertParams.positiveButtonText
            }

            showNegativeButton -> {
                bottomGuideLine.visibility = View.VISIBLE
                positiveButton.visibility = View.GONE
                negativeButton.visibility = View.VISIBLE
                negativeButton.text = alertParams.negativeButtonText
            }

            else -> {
                val layoutParams = bottomGuideLine.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.guideEnd = 0
                bottomGuideLine.requestLayout()
                bottomGuideLine.visibility = View.GONE
            }
        }
    }

    private fun bindButtonClick(buttonWhat: Int, listener: DialogInterface.OnClickListener?) {
        listener?.let {
            buttonHandler.obtainMessage(buttonWhat, listener).sendToTarget()
        }
        buttonHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG, dialog).sendToTarget()
    }

    private class ItemAdapter(
        private val dialog: AppCompatDialog,
        private val arrays: List<AppBottomDialogParams.Item>,
        @LayoutRes private val itemLayoutRes: Int,
        private val itemsVisibleDivider: Boolean,
        private val itemClickListener: DialogInterface.OnClickListener?
    ) : RecyclerView.Adapter<ItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    itemLayoutRes, parent, false
                )
            )
        }

        override fun getItemCount(): Int {
            return arrays.size
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.childItem.apply {
                val data = arrays[position]
                text = data.text
                isSelected = data.selected
                changeTextColor(
                    normalColor = ContextCompat.getColor(this.context, data.textColor)
                )
                if (data.leftIcon != null) {
                    setIcon1(data.leftIcon)
                }
                setOnClickListener {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(dialog, holder.adapterPosition)
                    } else {
                        data.clickListener?.invoke()
                    }
                    dialog.dismiss()
                }
            }
            holder.divider.isVisible = itemsVisibleDivider && position != arrays.size - 1
        }
    }

    private class ItemViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childItem: SkyStateButton = itemView.findViewById(R.id.content_view)
        val divider: View = itemView.findViewById(R.id.divider)
    }

    fun setAlertParams(alertParams: AppBottomDialogParams.AlertParams) {
        this.alertParams = alertParams
    }

    private class ButtonHandler(dialog: DialogInterface) : Handler(Looper.getMainLooper()) {
        private val dialog: WeakReference<DialogInterface> = WeakReference(dialog)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL -> (msg.obj as DialogInterface.OnClickListener).onClick(
                    dialog.get(), msg.what
                )

                MSG_DISMISS_DIALOG -> (msg.obj as DialogInterface).dismiss()
            }
        }

        companion object {
            const val MSG_DISMISS_DIALOG = 1
        }
    }

    companion object {
        @SuppressLint("NewApi")
        private fun canTextInput(view: View): Boolean {
            var targetView = view
            if (targetView.onCheckIsTextEditor()) {
                return true
            }
            if (targetView !is ViewGroup) {
                return false
            }
            val viewGroup: ViewGroup = targetView
            var i = viewGroup.childCount
            while (i > 0) {
                i--
                targetView = viewGroup.getChildAt(i)
                if (canTextInput(targetView)) {
                    return true
                }
            }
            return false
        }
    }

    fun getDimenPx(context: Context, resId: Int): Int {
        return context.resources.getDimensionPixelSize(resId)
    }
}