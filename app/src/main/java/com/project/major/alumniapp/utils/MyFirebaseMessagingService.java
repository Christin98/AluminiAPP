
package com.project.major.alumniapp.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.major.alumniapp.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIIDService";
    private static int notificationId = 0;
    private static final String POST_ID_KEY = "postId";
    private static final String AUTHOR_ID_KEY = "authorId";
    private static final String ACTION_TYPE_KEY = "actionType";
    private static final String TITLE_KEY = "title";
    private static final String BODY_KEY = "message";
    private static final String ICON_KEY = "icon";
    private static final String ACTION_TYPE_NEW_LIKE = "new_like";
    private static final String ACTION_TYPE_NEW_COMMENT = "new_comment";
    private static final String ACTION_TYPE_NEW_POST = "new_post";
    private static final String ACTION_TYPE_ADMIN = "admin";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, s);
        sendRegistrationToServer(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        remoteMessage.getData();
        if (remoteMessage.getData().get(ACTION_TYPE_KEY) != null) {
            handleRemoteMessage(remoteMessage);
        } else {
            Log.e(TAG, "onMessageReceived()", new RuntimeException("FCM remoteMessage doesn't contains Action Type"));
        }

    }

    private void handleRemoteMessage(RemoteMessage remoteMessage) {
        String receivedActionType = remoteMessage.getData().get(ACTION_TYPE_KEY);
        Log.d(TAG, "Message Notification Action Type: " + receivedActionType);

        switch (receivedActionType) {
            case ACTION_TYPE_NEW_LIKE:
                parseLike(remoteMessage);
                break;
            case ACTION_TYPE_NEW_COMMENT:
                parseComment(remoteMessage);
                break;
            case ACTION_TYPE_NEW_POST:
                handleNewPostCreatedAction(remoteMessage);
                break;
            case ACTION_TYPE_ADMIN:
                parseAdmin(remoteMessage);
                break;
        }
    }

    private void parseAdmin(RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        String msg = null ;
        switch (type) {
            case "request":
                msg = "requested to be admin of your city.";
                break;
            case "accepted":
                msg = "accepted your request to be admin.";
                break;
            case "rejected":
                msg = "rejected your request to be admin.";
                break;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "admin");
        notificationBuilder.setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.notification_ic) //Notification icon
//                .setContentIntent(pendingIntent)
                .setContentTitle(remoteMessage.getData().get(TITLE_KEY))
                .setContentText(remoteMessage.getData().get("message") + " " + msg)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setPriority(1)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("admin", "admin", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(this, R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId("admin");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    private void parseComment(RemoteMessage remoteMessage) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "new_comment");
        notificationBuilder.setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.notification_ic) //Notification icon
//                .setContentIntent(pendingIntent)
                .setContentTitle(remoteMessage.getData().get(TITLE_KEY))
                .setContentText(remoteMessage.getData().get("body") + " commented " + "\""+remoteMessage.getData().get(BODY_KEY)+"\"" + " on your post.")
                .setLargeIcon(getBitmapFromUrl(remoteMessage.getData().get("icon2")))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setPriority(1)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("new_comment", "new_comment", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(this, R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId("new_comment");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    private void parseLike(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "new_like");
        notificationBuilder.setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.notification_ic) //Notification icon
//                .setContentIntent(pendingIntent)
                .setContentTitle(remoteMessage.getData().get(TITLE_KEY))
                .setContentText(remoteMessage.getData().get(BODY_KEY))
                .setLargeIcon(getBitmapFromUrl(remoteMessage.getData().get(ICON_KEY)))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setPriority(1)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("new_like", "new_like", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(this, R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId("new_like");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    public void handleNewPostCreatedAction(RemoteMessage remoteMessage) {
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        PendingIntent pendingIntent;
//
//        if (backIntent != null) {
//            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Intent[] intents = new Intent[]{backIntent, intent};
//            pendingIntent = PendingIntent.getActivities(this, notificationId++, intents, PendingIntent.FLAG_ONE_SHOT);
//        } else {
//            pendingIntent = PendingIntent.getActivity(this, notificationId++, intent, PendingIntent.FLAG_ONE_SHOT);
//        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "new_post");
        notificationBuilder.setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.notification_ic) //Notification icon
//                .setContentIntent(pendingIntent)
                .setContentTitle(remoteMessage.getData().get(TITLE_KEY))
                .setContentText(remoteMessage.getData().get(BODY_KEY))
                .setLargeIcon(getBitmapFromUrl(remoteMessage.getData().get(ICON_KEY)))
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setPriority(1)
                .setSound(defaultSoundUri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("new_post", "new_post", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(this, R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId("new_post");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(notificationId++, notificationBuilder.build());
    }

    @Nullable
    public Bitmap getBitmapFromUrl(String imageUrl) {
        return ImageUtils.loadBitmap(GlideApp.with(this), imageUrl, 256, 256);
    }
    private void sendRegistrationToServer(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(user.getUid());
        reference.child("token").setValue(token);
    }

//    enum Channel {
////        NEW_LIKE("new_like_id", R.string.new_like_channel_name),
////        NEW_COMMENT("new_comment_id", R.string.new_comment_channel_name);
//
//        String id;
//        @StringRes
//        int name;
//
//        Channel(String id, @StringRes int name) {
//            this.id = id;
//            this.name = name;
//        }
//    }
}
