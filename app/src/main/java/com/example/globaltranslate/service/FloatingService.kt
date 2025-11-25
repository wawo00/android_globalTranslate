package com.example.globaltranslate.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import com.example.globaltranslate.ActivityTracker
import com.example.globaltranslate.LayoutInspectorUtils
import com.example.globaltranslate.MainActivity
import com.example.globaltranslate.R
import com.example.globaltranslate.databinding.LayoutFloatingWindowBinding
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern

/**
 * 前台服务，管理悬浮窗
 */
class FloatingService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "FloatingServiceChannel"
        private const val FLOAT_TAG = "LayoutInspectorFloat"
        var isServiceRunning = false
        const val ACTION_CHANGE_TEXT_COLOR = "com.example.globaltranslate.CHANGE_TEXT_COLOR"
        private  const val  TAG="FloatingService"
    }

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        createNotificationChannel()
        showFloatingWindow()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 创建前台通知
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        // 移除悬浮窗
        EasyFloat.dismiss(FLOAT_TAG)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 创建前台服务通知
     */
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_content))
            .setSmallIcon(R.drawable.ic_inspect)
            .setContentIntent(pendingIntent)
            .build()
    }

    /**
     * 显示悬浮窗
     */
    private fun showFloatingWindow() {
        EasyFloat.with(this)
            .setTag(FLOAT_TAG)
            .setLayout(R.layout.layout_floating_window) { view ->
                // 使用ViewBinding绑定悬浮窗布局
//                view.setOnClickListener{
//                    onFloatingButtonClicked()
//                }
//                val binding = LayoutFloatingWindowBinding.bind(view)
//                // 设置点击事件
               view.findViewById<CardView>(R.id.cardView).findViewById<ImageView>(R.id.ivFloatingIcon).setOnClickListener {
                    // 点击悬浮窗按钮时，触发修改文字颜色的操作
                    onFloatingButtonClicked()
                }

            }
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setDragEnable(true)
            .show()
    }

    /**
     * 悬浮窗按钮点击事件
     */
    private fun onFloatingButtonClicked() {
//        // 通知无障碍服务修改文字颜色
//        val intent = Intent(ACTION_CHANGE_TEXT_COLOR)
//        sendBroadcast(intent)
        // 使用你原来的 LayoutInspectorUtils
        ActivityTracker.currentActivity?.let {
            Toast.makeText(this, "正在检查布局...", Toast.LENGTH_SHORT).show()
            val json = LayoutInspectorUtils.exportViewHierarchyToJson(it)
            Log.d(TAG, "View 层级结构:\n$json")
        }?: kotlin.run {
            Toast.makeText(this, "正在检查布局...", Toast.LENGTH_SHORT).show()
            Log.i(TAG, "activity为null: ")
        }
    }

}
