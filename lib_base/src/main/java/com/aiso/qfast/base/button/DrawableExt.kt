package com.aiso.qfast.base.button

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

@SuppressLint("NewApi")
fun Drawable?.tint(@ColorInt color: Int): Drawable? {
    if (this == null) {
        return null
    }
    val wrappedDrawable = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTint(wrappedDrawable, color)
    return wrappedDrawable
}

@SuppressLint("NewApi")
fun Drawable?.tint(color: ColorStateList): Drawable? {
    if (this == null) {
        return null
    }
    val wrappedDrawable = DrawableCompat.wrap(this.mutate())
    DrawableCompat.setTintList(wrappedDrawable, color)
    return wrappedDrawable
}