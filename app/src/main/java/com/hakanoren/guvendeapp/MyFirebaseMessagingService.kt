package com.hakanoren.guvendeapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        //Log.d(TAG, "Refreshed token: $token")

        sendRegistrationToServer(token)
    }
    private fun sendRegistrationToServer(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/token")
            databaseRef.setValue(token)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Log.d(TAG, "Token successfully sent to the server.")
                    } else {
                        //Log.e(TAG, "Failed to send token to the server: ${task.exception?.message}")
                    }
                }
        } else {
            //Log.e(TAG, "User not logged in. Cannot send token to the server.")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Gelen mesajın işlenmesi
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel_id"
        val defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo_background) // Bildirim simgesi (uygun bir ikon koyduğunuzdan emin olun)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android Oreo (API 26) ve üstü için bildirim kanalı oluştur
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Default Channel"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}