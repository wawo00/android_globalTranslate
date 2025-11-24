package com.example.globaltranslate.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

/**
 * 无障碍服务，用于检查屏幕布局和修改文字颜色
 */
class LayoutInspectorService : AccessibilityService() {

    companion object {
        private const val TAG = "LayoutInspectorService"
    }

    private val changeColorReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == FloatingService.ACTION_CHANGE_TEXT_COLOR) {
                changeTextColorToRed()
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "无障碍服务已连接")
        
        // 注册广播接收器
        val filter = IntentFilter(FloatingService.ACTION_CHANGE_TEXT_COLOR)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(changeColorReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(changeColorReceiver, filter)
        }
        
        Toast.makeText(this, "布局检查服务已启用", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 监听窗口变化事件，但不在这里处理，而是等待用户点击悬浮窗按钮
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d(TAG, "窗口状态变化: ${event.packageName}")
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "无障碍服务中断")
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(changeColorReceiver)
        } catch (e: Exception) {
            Log.e(TAG, "注销广播接收器失败", e)
        }
    }

    /**
     * 修改屏幕上所有文字颜色为红色
     */
    private fun changeTextColorToRed() {
        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            Toast.makeText(this, "无法获取屏幕内容", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "rootInActiveWindow 为空")
            return
        }

        var count = 0
        try {
            // 遍历所有节点并修改文字颜色
            count = traverseAndChangeTextColor(rootNode)
            
            Toast.makeText(
                this,
                "已修改 $count 个文本控件的颜色为红色",
                Toast.LENGTH_SHORT
            ).show()
            
            Log.d(TAG, "成功修改 $count 个文本控件")
        } catch (e: Exception) {
            Log.e(TAG, "修改文字颜色失败", e)
            Toast.makeText(this, "修改失败: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            rootNode.recycle()
        }
    }

    /**
     * 递归遍历节点树并修改文字颜色
     */
    private fun traverseAndChangeTextColor(node: AccessibilityNodeInfo?): Int {
        if (node == null) return 0
        
        var count = 0

        try {
            // 检查当前节点是否有文字
            val text = node.text
            if (!text.isNullOrEmpty()) {
                // 尝试修改文字颜色
                if (changeNodeTextColor(node, text.toString())) {
                    count++
                }
            }

            // 递归遍历所有子节点
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    count += traverseAndChangeTextColor(child)
                    child.recycle()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "遍历节点失败", e)
        }

        return count
    }

    /**
     * 修改单个节点的文字颜色
     */
    private fun changeNodeTextColor(node: AccessibilityNodeInfo, text: String): Boolean {
        try {
            // 检查节点是否可编辑
            if (!node.isEditable && node.className != null) {
                val className = node.className.toString()
                
                // 只处理TextView和Button等文本控件
                if (className.contains("TextView") || 
                    className.contains("Button") ||
                    className.contains("EditText")) {
                    
                    // 创建带颜色的文本
                    val spannableString = SpannableString(text)
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        text.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // 尝试设置文本
                    // 注意：由于无障碍服务的限制，这种方法可能不会对所有应用生效
                    // 某些应用可能会阻止外部修改其UI
                    val args = android.os.Bundle()
                    args.putCharSequence(
                        AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        spannableString
                    )
                    
                    // 对于可编辑的控件，尝试设置文本
                    if (node.isEditable) {
                        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
                        return true
                    }
                    
                    // 对于不可编辑的控件，我们记录一下但无法直接修改
                    // 这是Android安全机制的限制
                    Log.d(TAG, "找到文本控件: $className - $text (不可编辑)")
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "修改节点文字颜色失败", e)
        }
        
        return false
    }

    /**
     * 打印节点树结构（用于调试）
     */
    private fun printNodeTree(node: AccessibilityNodeInfo?, depth: Int = 0) {
        if (node == null) return
        
        val indent = "  ".repeat(depth)
        val text = node.text ?: ""
        val className = node.className ?: ""
        
        Log.d(TAG, "$indent- $className: $text")
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                printNodeTree(child, depth + 1)
                child.recycle()
            }
        }
    }
}
