package ru.pwssv67.healthcounter.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import ru.pwssv67.healthcounter.App

object NotificationHandler {
    val notificationManager:NotificationManager
    val notificationChannel:NotificationChannel
    init {
        notificationManager = App.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(App.NOTIFICATION_CHANNEL_1_ID) == null) {
            notificationChannel = NotificationChannel(App.NOTIFICATION_CHANNEL_1_ID, "Good Weather Notification", NotificationManager.IMPORTANCE_LOW)
            notificationChannel.description = "Description"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        } else {
            notificationChannel = notificationManager.getNotificationChannel(App.NOTIFICATION_CHANNEL_1_ID)
        }
    }

    fun showNotification(notification: Notification, notificationID: Int) {
        notificationManager.notify(notificationID, notification)
    }
}