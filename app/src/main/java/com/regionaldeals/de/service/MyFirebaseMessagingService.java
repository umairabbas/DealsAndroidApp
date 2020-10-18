package com.regionaldeals.de.service;

/**
 * Created by Umi on 14.01.2018.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.regionaldeals.de.R;
import com.regionaldeals.de.SplashActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

//
//    private static final String TAG = "MyFirebaseMsgService";
//    Bitmap bitmap;
//
//    /**
//     * Called when message is received.
//     *
//     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
//     */
//    // [START receive_message]
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        //Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//        }
//
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//
//        sendNotification(remoteMessage);
//    }
//
//    private void sendNotification(RemoteMessage remoteMessage) {
//
//        String dealids = remoteMessage.getData().get("dealids");
//        String gutid = remoteMessage.getData().get("gutscheinid");
//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();
//
//        Intent intent = new Intent(this.getApplicationContext(), SplashActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if (dealids != null) {
//            intent.putExtra("notificationBody", dealids);
//        }
//        if (gutid != null) {
//            intent.putExtra("notificationGut", gutid);
//        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//            String id = "deals_channel";
//            CharSequence name = "Deals Channel";
//            String description = "Get latest deals notifications in your area";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//            NotificationChannel mChannel = new NotificationChannel(id, name,importance);
//            mChannel.setDescription(description);
//            mChannel.enableLights(true);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            mNotificationManager.createNotificationChannel(mChannel);
//
//            String channelId = getString(R.string.default_notification_channel_id);
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, channelId)
//                            .setSmallIcon(R.drawable.ic_launcher_not)
//                            //.setLargeIcon(bmpic)/*Notification icon image*/
//                            .setContentTitle(title)
//                            .setContentText(body)
//                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                            //.setStyle(new NotificationCompat.BigPictureStyle()
//                            //       .bigPicture(bmpic))/*Notification with Image*/
//                            .setAutoCancel(true)
//                            .setSound(defaultSoundUri)
//                            .setChannelId(mChannel.getId())
//                            .setContentIntent(pendingIntent);
//            mNotificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//        } else {
//
//            String channelId = getString(R.string.default_notification_channel_id);
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, channelId)
//                            .setSmallIcon(R.drawable.ic_launcher_not)
//                            //.setLargeIcon(bmpic)/*Notification icon image*/
//                            .setContentTitle(title)
//                            .setContentText(body)
//                            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                            //.setStyle(new NotificationCompat.BigPictureStyle()
//                            //       .bigPicture(bmpic))/*Notification with Image*/
//                            .setAutoCancel(true)
//                            .setSound(defaultSoundUri)
//                            .setContentIntent(pendingIntent);
//            mNotificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//        }
//
//    }
}