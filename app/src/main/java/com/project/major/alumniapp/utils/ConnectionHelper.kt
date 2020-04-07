/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 3                                                                          *
 */
@file:Suppress("DEPRECATION")

package com.project.major.alumniapp.utils

import android.content.Context
import android.net.ConnectivityManager
import java.util.*

object ConnectionHelper {
    var lastNoConnectionTs: Long = -1
    var isOnline = true
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = Objects.requireNonNull(cm).activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun isConnectedOrConnecting(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = Objects.requireNonNull(cm).activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}