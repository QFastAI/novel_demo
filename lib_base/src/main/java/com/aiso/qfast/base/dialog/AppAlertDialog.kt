package com.aiso.qfast.base.dialog

import android.content.Context
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.aiso.qfast.base.R

class AppAlertDialog(context: Context) : AppCompatDialog(context, R.style.theme_dialog_alert) {

    internal val alertController: AppAlertController? = window?.let {
        AppAlertController(
            context, this, it
        )
    }

    class Builder(context: Context?) : AppDialogParams.AppAlertBuilder<AppAlertDialog>(context) {
        override fun internalCreate(): AppAlertDialog? {
            if (context == null) {
                return null
            }
            val dialog = AppAlertDialog(context)
            dialog.setContentView(R.layout.theme_app_alert_dialog_layout)
            dialog.alertController?.setAlertParams(alertParams)
            dialog.setCancelable(alertParams.cancelable)
            dialog.window?.setBackgroundDrawableResource(alertParams.dialogBackgroundResId)
            if (alertParams.cancelable) {
                dialog.setCanceledOnTouchOutside(true)
            }
            dialog.setOnCancelListener(alertParams.cancelListener)
            dialog.setOnDismissListener(alertParams.dismissListener)
            dialog.alertController?.setupView()
            if (softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
                dialog.window?.setSoftInputMode(softInputMode)
            }

            if (dialog.alertController?.canTextInput() == false) {
                dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            }
            return dialog
        }
    }
}

