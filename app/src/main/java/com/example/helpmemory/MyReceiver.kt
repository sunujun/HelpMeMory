package com.example.helpmemory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class MyReceiver : BroadcastReceiver() {

    lateinit var notificationManager: NotificationManager
    lateinit var keywordTitle: String
    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        keywordTitle = intent.getStringExtra("title").toString()
        createNotificationChannel()
        deliverNotification(context)
    }

    // Notification 을 띄우기 위한 Channel 등록
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "notification_channel", // 채널의 아이디
                "키워드 알림", // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true) // 불빛
            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = "에빙하우스 망각 곡선에 따른 키워드 알림입니다" // 채널 정보
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    // Notification 등록
    private fun deliverNotification(context: Context){
        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            (System.currentTimeMillis()).toInt(), // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "notification_channel")
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24) // 아이콘
            .setContentTitle(keywordTitle) // 제목
            .setContentText("공부 할 시간이에요") // 내용
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        // 랜덤으로 줘야 세팅한 모든 알림이 발생됨
        notificationManager.notify((System.currentTimeMillis()).toInt(), builder.build())
    }
}