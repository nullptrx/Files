package io.github.nullptrx.files.example.notification

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.util.NotificationChannelTemplate
import io.github.nullptrx.files.util.NotificationTemplate

val fileJobNotificationTemplate: NotificationTemplate =
  NotificationTemplate(
    NotificationChannelTemplate(
      "file_job",
      R.string.notification_channel_file_job_name,
      NotificationManagerCompat.IMPORTANCE_LOW,
      descriptionRes = R.string.notification_channel_file_job_description,
      showBadge = false
    ),
    colorRes = R.color.color_primary,
    smallIcon = R.drawable.notification_icon,
    ongoing = true,
    category = NotificationCompat.CATEGORY_PROGRESS,
    priority = NotificationCompat.PRIORITY_LOW
  )
