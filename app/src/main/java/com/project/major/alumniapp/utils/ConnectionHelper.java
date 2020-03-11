/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 3                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class ConnectionHelper {
    public static long lastNoConnectionTs = -1;
    public static boolean isOnline = true;

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public static boolean isConnectedOrConnecting(Context context) {
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
