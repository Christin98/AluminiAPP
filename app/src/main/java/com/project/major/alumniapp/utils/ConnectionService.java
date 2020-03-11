/******************************************************************************
 * Copyright (c) 2020.                                                        *
 * Christin B Koshy.                                                          *
 * 2                                                                          *
 ******************************************************************************/

package com.project.major.alumniapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.project.major.alumniapp.R;
import com.sdsmdg.tastytoast.TastyToast;


public class ConnectionService extends Service {

    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    NotificationCompat.Builder builder;
    NotificationManager manager ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (CONNECTIVITY_CHANGE_ACTION.equals(action)) {
                    //check internet connection
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        boolean show = false;
                        if (ConnectionHelper.lastNoConnectionTs == -1) {//first time
                            show = true;
                            ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                        } else {
                            if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                show = true;
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis();
                            }
                        }

                        if (show && ConnectionHelper.isOnline) {
                            ConnectionHelper.isOnline = false;
                            Log.i("NETWORK123","Connection lost");

                            AlertDialog builder;
                             builder = new AlertDialog.Builder(context)
                                    .setTitle("NO CONNECTION")
                                    .setMessage("No network connection available. Please connect to internet and try again")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .create();
                            builder.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            builder.show();
                        }
                    } else {
                        Log.i("NETWORK123","Connected");
                        showNotifications(context);
                        Toast tastyToast = TastyToast.makeText(context, "Connected to network", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                        ConnectionHelper.isOnline = true;
                    }
                }
            }
        };
        registerReceiver(receiver,filter);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    private void showNotifications(Context context){
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("alumni_app_net_1", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);
        }
        builder = new NotificationCompat.Builder(context, "alumni_app_net_1");
        Notification notification = builder.setContentTitle("APP")
                .setContentText("Connected to Internet")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);

        new Handler().postDelayed(() -> manager.cancel(1), 8000);
    }
}
