package com.aiso.qfast.base.dialog
import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import com.aiso.qfast.base.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class AppBottomDialog(context: Context) :
    BottomSheetDialog(context, R.style.theme_bottom_sheet_dialog) {

    internal val alertController: AppBottomAlertController? = window?.let {
        AppBottomAlertController(
            context, this, it
        )
    }

    class Builder(context: Context?) : AppBottomDialogParams.AppAlertBuilder<AppBottomDialog>(context) {

        @SuppressLint("NewApi")
        override fun internalCreate(): AppBottomDialog? {
            if (context == null) {
                return null
            }
            val dialog = AppBottomDialog(context)
            dialog.setContentView(R.layout.theme_app_bottom_dialog_layout)
            if (alertParams.itemLayoutRes == null) {
                alertParams.itemLayoutRes = R.layout.theme_bottom_dialog_list_item_layout
            }
            dialog.alertController?.setAlertParams(alertParams)
            dialog.setCancelable(alertParams.cancelable)
            if (alertParams.cancelable) {
                dialog.setCanceledOnTouchOutside(true)
            }
            dialog.setOnCancelListener(alertParams.cancelListener)
            dialog.setOnDismissListener(alertParams.dismissListener)
            dialog.alertController?.setupView()
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            if (softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                dialog.window?.setSoftInputMode(softInputMode)
            }
            return dialog
        }
    }
}

