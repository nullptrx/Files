package io.github.nullptrx.files.example.notification

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.util.NotificationChannelTemplate
import io.github.nullptrx.files.util.NotificationTemplate

val ftpServerServiceNotificationTemplate: NotificationTemplate =
  NotificationTemplate(
    NotificationChannelTemplate(
      "ftp_server",
      R.string.notification_channel_ftp_server_name,
      NotificationManagerCompat.IMPORTANCE_LOW,
      descriptionRes = R.string.notification_channel_ftp_server_description,
      showBadge = false
    ),
    colorRes = R.color.color_primary,
    smallIcon = R.drawable.notification_icon,
    contentTitleRes = R.string.ftp_server_notification_title,
    contentTextRes = R.string.ftp_server_notification_text,
    ongoing = true,
    category = NotificationCompat.CATEGORY_SERVICE,
    priority = NotificationCompat.PRIORITY_LOW
  )
//
// object FtpServerServiceNotification {
//   fun startForeground(service: Service) {
//     val contentIntent = FtpServerActivity::class.createIntent()
//     var pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT
//     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//       pendingIntentFlags = pendingIntentFlags or PendingIntent.FLAG_IMMUTABLE
//     }
//     val contentPendingIntent = PendingIntent.getActivity(
//       service, FtpServerActivity::class.hashCode(), contentIntent, pendingIntentFlags
//     )
//     val stopIntent = FtpServerReceiver.createIntent()
//     val stopPendingIntent = PendingIntent.getBroadcast(
//       service, FtpServerReceiver::class.hashCode(), stopIntent, pendingIntentFlags
//     )
//     val notification = ftpServerServiceNotificationTemplate.createBuilder(service)
//       .setContentIntent(contentPendingIntent)
//       .addAction(
//         R.drawable.stop_icon_white_24dp, service.getString(R.string.stop),
//         stopPendingIntent
//       )
//       .build()
//     service.startForeground(NotificationIds.FTP_SERVER, notification)
//   }
//
//   fun stopForeground(service: Service) {
//     service.stopForeground(true)
//   }
// }
