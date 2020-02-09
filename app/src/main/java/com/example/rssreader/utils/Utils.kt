package com.example.rssreader.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.widget.ViewAnimator

fun ViewAnimator.showChild(view: View) {
    indexOfChild(view).also {
        if (displayedChild != it) {
            displayedChild = it
        }
    }
}

fun hasInternetConnection(context: Context): Boolean {
    val cm: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm?.activeNetworkInfo
    return activeNetwork?.isConnectedOrConnecting ?: false
}
