package com.github.kutyrev.intervals.utils

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable

const val LIST_KEY = "list"
const val SDK_CHANGE_VERSION = 33

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= SDK_CHANGE_VERSION -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}
