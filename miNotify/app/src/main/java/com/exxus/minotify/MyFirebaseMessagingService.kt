package com.exxus.minotify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

//    override fun onMessageReceived(remoteMessage: RemoteMessage)  {
//        sendNotification(remoteMessage.data["title"]!!, remoteMessage.data["body"]!!)
//    }

    private fun newPicture(myArray: String):Bitmap {

        val result = myArray
            .removeSurrounding("[", "]")
            .split(",")
            .map { it.toInt() }
 //       Log.d(TAG, "Result array: $result")
        val bitmap = Bitmap.createBitmap(
            180, 91,
            Bitmap.Config.ARGB_8888
        )
        bitmap.eraseColor(Color.LTGRAY)

        val w: Int = bitmap.width
        val h: Int = bitmap.height

        for (x in 0 until w) {
            bitmap.setPixel(x, h-16, Color.WHITE)
            bitmap.setPixel(x, h-31, Color.WHITE)
            bitmap.setPixel(x, h-46, Color.WHITE)
            bitmap.setPixel(x, h-61, Color.WHITE)
            bitmap.setPixel(x, h-76, Color.WHITE)
        }

        for (x in 0 until w) {
            for (y in 0 until h) {
                if (y <= result[x]*0.15) bitmap.setPixel(x, h-y-1, Color.BLUE)
            }
        }
        for (y in 0 until h) {
            bitmap.setPixel(10, y, Color.LTGRAY)
            bitmap.setPixel(11, y, Color.BLACK)
        }
        for (x in 0 until w) {
            bitmap.setPixel(x, h-1, Color.LTGRAY)
            bitmap.setPixel(x, h-2, Color.BLACK)
        }
 return bitmap
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val data = remoteMessage.data
        val title = data["title"]
        val body = data["body"]
        val myJsonArray = data["myJsonArray"]


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()

            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(
            title!!,
            body!!,
            myJsonArray!!
        )
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageTitle: String, messageBody: String, myArray: String) {

//       val bundle_notification_id = "bundle_notification_" + bundleNotificationId;

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val beeper = LongArray(4) { 500 }
        val intent = Intent(this, SettingsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        //We need to update the bundle notification every time a new notification comes up.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.notificationChannels.size < 2) {
                val groupChannel = NotificationChannel(
                    "bundle_channel_id",
                    "bundle_channel_name",
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(groupChannel)
                val channel = NotificationChannel(
                    "channel_id",
                    "channel_name",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

//        val summaryNotificationBuilder = NotificationCompat
//            .Builder(this, "bundle_channel_id")
//            .setGroup(bundle_notification_id)
//            .setGroupSummary(true)
//            .setContentTitle(messageTitle)
//            .setContentText(messageBody)
//            .setSmallIcon(R.drawable.ic_stat_ic_notification)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(false)
//            .setSound(defaultSoundUri)
//            .setVibrate(beeper)
//            .setLights(Color.MAGENTA, 500, 1000)
//            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(newPicture(myArray)))
//
//        if (singleNotificationId == bundleNotificationId) singleNotificationId =
//            bundleNotificationId + 1 else singleNotificationId++
        singleNotificationId++


        val notification =
            NotificationCompat.Builder(this, "channel_id")
 //               .setGroup(bundle_notification_id)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setGroupSummary(false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setVibrate(beeper)
                .setLights(Color.MAGENTA, 500, 1000)
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(newPicture(myArray)))

        NotificationManagerCompat.from(this).apply {
            notify(singleNotificationId, notification.build())
//            notify(bundleNotificationId, summaryNotificationBuilder.build())
        }
//        Log.d(TAG, "bundleNotificationId($bundleNotificationId)")
        Log.d(TAG, "singleNotificationId($singleNotificationId)")

    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
//        var bundleNotificationId = 100
        var singleNotificationId = 100
    }
}

//
//val notificationBuilder = NotificationCompat.Builder(this, bundle_notification_id)
//    .setSmallIcon(R.drawable.ic_stat_ic_notification)
//    .setContentTitle(messageTitle)
//    .setContentText(messageBody)
//    .setAutoCancel(true)
//    .setSound(defaultSoundUri)
//    .setVibrate(beeper)
//    .setLights(Color.MAGENTA, 500, 1000)
//    .setContentIntent(pendingIntent)
//    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(newPicture()))
//    .setGroup(MyFirebaseMessagingService.GROUP_KEY_WORK_EMAIL)
//    .setGroupSummary(true)
//
//
//
//// Since android Oreo notification channel is needed.
//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//    val channel = NotificationChannel(bundle_notification_id,
//        "Channel human readable title",
//        NotificationManager.IMPORTANCE_DEFAULT)
//    notificationManager.createNotificationChannel(channel)
//}
//
//notificationManager.notify(SUMMARY_ID, notificationBuilder.build())