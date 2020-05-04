@file:Suppress("DEPRECATION")

package com.project.major.alumniapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.project.major.alumniapp.R
import com.sdsmdg.tastytoast.TastyToast

class ConnectionService : Service() {
    var builder: NotificationCompat.Builder? = null
    var manager: NotificationManager? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (CONNECTIVITY_CHANGE_ACTION == action) {
                    //check internet connection
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        var show = false
                        if (ConnectionHelper.lastNoConnectionTs == -1L) { //first time
                            show = true
                            ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis()
                        } else {
                            if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                show = true
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis()
                            }
                        }
                        if (show && ConnectionHelper.isOnline) {
                            ConnectionHelper.isOnline = false
                            Log.i("NETWORK123", "Connection lost")
                            val tastyToast = TastyToast.makeText(context, "Internet Connection Not Available.\n Please Connect To Network and Retry.", TastyToast.LENGTH_LONG, TastyToast.WARNING)
                            tastyToast.setGravity(Gravity.CENTER, 0, 0)
                            tastyToast.show()
                        }
                    } else {
                        Log.i("NETWORK123", "Connected")
                        showNotifications(context)
                        val tastyToast = TastyToast.makeText(context, "Connected to network", TastyToast.LENGTH_LONG, TastyToast.INFO)
                        tastyToast.setGravity(Gravity.CENTER, 0, 0)
                        tastyToast.show()
                        ConnectionHelper.isOnline = true
                    }
                }
            }
        }
        registerReceiver(receiver, filter)
        return START_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show()
        super.onDestroy()
    }

    private fun showNotifications(context: Context) {
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("alumni_app_net_1", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT)

            // Configure the notification channel.
            notificationChannel.description = "Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            manager!!.createNotificationChannel(notificationChannel)
        }
        builder = NotificationCompat.Builder(context, "alumni_app_net_1")
        val notification = builder!!.setContentTitle("APP")
                .setContentText("Connected to Internet")
                .setVibrate(longArrayOf(0, 1000, 500, 1000))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build()
        manager!!.notify(1, notification)
        Handler().postDelayed({ manager!!.cancel(1) }, 8000)
    }

    companion object {
        const val CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}