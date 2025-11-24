package com.example.globaltranslate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.globaltranslate.databinding.ActivityMainBinding
import com.example.globaltranslate.service.FloatingService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_REQUEST_CODE = 1001
    private val OVERLAY_PERMISSION_REQUEST_CODE = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        updateStatus()
    }

    private fun setupClickListeners() {
        // 请求权限按钮
        binding.btnRequestPermissions.setOnClickListener {
            requestPermissions()
        }

        // 启用无障碍服务按钮
        binding.btnEnableAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        // 启动服务按钮
        binding.btnStartService.setOnClickListener {
            if (checkPermissions()) {
                startFloatingService()
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show()
            }
        }

        // 停止服务按钮
        binding.btnStopService.setOnClickListener {
            stopFloatingService()
        }
    }

    /**
     * 请求必要的权限
     */
    private fun requestPermissions() {
        // 请求通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        }

        // 请求悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 检查所有必要权限是否已授予
     */
    private fun checkPermissions(): Boolean {
        // 检查悬浮窗权限
        if (!Settings.canDrawOverlays(this)) {
            return false
        }

        // 检查通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }

    /**
     * 打开无障碍服务设置页面
     */
    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "请找到并启用 '全局翻译' 服务", Toast.LENGTH_LONG).show()
    }

    /**
     * 启动前台服务
     */
    private fun startFloatingService() {
        val intent = Intent(this, FloatingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        binding.tvStatus.text = "状态：服务运行中"
        Toast.makeText(this, R.string.service_started, Toast.LENGTH_SHORT).show()
    }

    /**
     * 停止前台服务
     */
    private fun stopFloatingService() {
        val intent = Intent(this, FloatingService::class.java)
        stopService(intent)
        binding.tvStatus.text = "状态：服务已停止"
        Toast.makeText(this, R.string.service_stopped, Toast.LENGTH_SHORT).show()
    }

    /**
     * 更新状态文本
     */
    private fun updateStatus() {
        val isRunning = FloatingService.isServiceRunning
        binding.tvStatus.text = if (isRunning) {
            "状态：服务运行中"
        } else {
            "状态：未启动"
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "通知权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "悬浮窗权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "悬浮窗权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
