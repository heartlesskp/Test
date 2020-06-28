package com.expertbrains.startegictest.widgets

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.expertbrains.startegictest.R

object CustomDialog {
    fun showDialog(
        context: Context,
        title: String,
        message: String,
        isCancelable: Boolean,
        positiveLabel: String,
        negativeLabel: String,
        showIcon: Boolean = false,
        onClickCallBack: OnClickCallback
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(isCancelable)
        if (showIcon)
            builder.setIcon(
                ContextCompat.getDrawable(context, R.drawable.logo)
            )
        builder.setPositiveButton(positiveLabel) { dialog, _ ->
            onClickCallBack.positiveOnclick()
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeLabel) { dialog, _ ->
            onClickCallBack.negativeOnClick()
            dialog.dismiss()
        }
        builder.create().show()
    }

    interface OnClickCallback {
        fun positiveOnclick()
        fun negativeOnClick()
    }
}