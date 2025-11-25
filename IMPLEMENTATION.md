# 实现细节说明

本文档详细说明了项目的技术实现细节，适合开发者阅读。

## 架构概述

```
┌─────────────────┐
│   MainActivity  │ - 主界面，处理权限请求
└────────┬────────┘
         │ 启动
         ▼
┌─────────────────┐
│ FloatingService │ - 前台服务，管理悬浮窗
└────────┬────────┘
         │ 显示
         ▼
┌─────────────────┐
│   EasyFloat     │ - 悬浮窗UI，接收用户点击
└────────┬────────┘
         │ 广播
         ▼
┌──────────────────────┐
│ LayoutInspectorService │ - 无障碍服务，检查布局
└──────────────────────┘
```

## 核心组件

### 1. MainActivity.kt

**职责:**
- 应用入口点
- 权限请求和管理
- 服务生命周期控制
- 用户界面交互

**关键代码片段:**

```kotlin
// 请求悬浮窗权限
private fun requestPermissions() {
    if (!Settings.canDrawOverlays(this)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
}

// 启动前台服务
private fun startFloatingService() {
    val intent = Intent(this, FloatingService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)  // Android 8.0+
    } else {
        startService(intent)
    }
}
```

**使用的技术:**
- ViewBinding - 类型安全的视图绑定
- ActivityCompat - 权限请求兼容性
- Intent - 组件间通信

### 2. FloatingService.kt

**职责:**
- 前台服务实现
- 通知管理
- 悬浮窗生命周期管理
- 广播通信

**关键实现:**

#### 前台服务通知

```kotlin
private fun createNotification(): Notification {
    val notificationIntent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("布局检查服务运行中")
        .setContentText("点击悬浮窗按钮可以修改屏幕文字颜色")
        .setSmallIcon(R.drawable.ic_inspect)
        .setContentIntent(pendingIntent)
        .build()
}
```

#### EasyFloat 集成

```kotlin
private fun showFloatingWindow() {
    EasyFloat.with(this)
        .setTag(FLOAT_TAG)
        .setLayout(R.layout.layout_floating_window) { view ->
            val binding = LayoutFloatingWindowBinding.bind(view)
            binding.ivFloatingIcon.setOnClickListener {
                onFloatingButtonClicked()
            }
        }
        .setShowPattern(ShowPattern.ALL_TIME)
        .setSidePattern(SidePattern.RESULT_SIDE)
        .setDragEnable(true)
        .show()
}
```

**EasyFloat 配置说明:**
- `ShowPattern.ALL_TIME` - 始终显示悬浮窗
- `SidePattern.RESULT_SIDE` - 自动吸附到屏幕边缘
- `setDragEnable(true)` - 允许拖动悬浮窗

#### 广播通信

```kotlin
private fun onFloatingButtonClicked() {
    val intent = Intent(ACTION_CHANGE_TEXT_COLOR)
    sendBroadcast(intent)
}
```

## 数据流

### 用户点击悬浮窗的完整流程

```
1. 用户点击悬浮窗
   │
   ▼
2. FloatingService.onFloatingButtonClicked()
   │
   ├─ 显示 Toast: "正在检查布局..."
   │
   └─ 发送广播: ACTION_CHANGE_TEXT_COLOR
      │
      ▼
3. LayoutInspectorService.changeColorReceiver.onReceive()
   │
   ├─ 调用 changeTextColorToRed()
   │  │
   │  ├─ 获取 rootInActiveWindow
   │  │
   │  └─ 调用 traverseAndChangeTextColor(rootNode)
   │     │
   │     ├─ 递归遍历所有节点
   │     │
   │     ├─ 识别文本控件
   │     │
   │     └─ 尝试修改颜色
   │
   └─ 显示结果 Toast
```

## 权限处理

### 权限类型和用途

| 权限 | 用途 | 请求时机 | 必需性 |
|------|------|----------|--------|
| FOREGROUND_SERVICE | 运行前台服务 | 清单文件声明 | 必需 |
| POST_NOTIFICATIONS | 显示通知 (Android 13+) | 运行时请求 | 必需 |
| SYSTEM_ALERT_WINDOW | 显示悬浮窗 | 运行时请求 | 必需 |
| BIND_ACCESSIBILITY_SERVICE | 绑定无障碍服务 | 用户手动授予 | 必需 |

### 权限请求流程

```kotlin
// 1. Android 13+ 通知权限
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        PERMISSION_REQUEST_CODE
    )
}

// 2. 悬浮窗权限
if (!Settings.canDrawOverlays(this)) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName")
    )
    startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
}

// 3. 无障碍服务（用户需要手动启用）
val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
startActivity(intent)
```

## ViewBinding 使用

### 配置

在 `app/build.gradle.kts` 中：

```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

### 在 Activity 中使用

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 使用 binding 访问视图
        binding.btnStartService.setOnClickListener {
            startFloatingService()
        }
    }
}
```

### 优势

- ✅ 编译时类型检查
- ✅ 避免 findViewById
- ✅ 空安全
- ✅ 更好的性能

## 依赖管理

### 核心依赖

```kotlin
dependencies {
    // AndroidX 核心
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    
    // 布局
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // 悬浮窗
    implementation("com.github.princekin-f:EasyFloat:2.0.4")
}
```

### 版本管理建议

使用版本目录 (Version Catalog):

创建 `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "1.9.22"
agp = "8.3.2"
androidx-core = "1.12.0"

[libraries]
androidx-core = { module = "androidx.core:core-ktx", version.ref = "androidx-core" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
```

## 性能优化

### 内存管理

```kotlin
// 及时回收 AccessibilityNodeInfo
private fun traverseAndChangeTextColor(node: AccessibilityNodeInfo?): Int {
    // ... 处理节点 ...
    
    for (i in 0 until node.childCount) {
        val child = node.getChild(i)
        if (child != null) {
            count += traverseAndChangeTextColor(child)
            child.recycle()  // ⚠️ 重要：回收节点
        }
    }
}
```

### 避免内存泄漏

```kotlin
// 在服务销毁时取消注册广播接收器
override fun onDestroy() {
    super.onDestroy()
    try {
        unregisterReceiver(changeColorReceiver)
    } catch (e: Exception) {
        Log.e(TAG, "注销广播接收器失败", e)
    }
}
```

## 调试技巧

### Logcat 过滤

```bash
# 查看特定标签
adb logcat -s LayoutInspectorService

# 查看多个标签
adb logcat -s LayoutInspectorService:D FloatingService:D

# 查看特定包名
adb logcat | grep "com.example.globaltranslate"
```

### 无障碍服务调试

```bash
# 检查服务是否运行
adb shell dumpsys accessibility

# 查看启用的无障碍服务
adb shell settings get secure enabled_accessibility_services
```

### 悬浮窗调试

```bash
# 检查悬浮窗权限
adb shell appops get com.example.globaltranslate SYSTEM_ALERT_WINDOW
```

## 测试建议

### 单元测试

测试业务逻辑，不依赖 Android 框架。

### 集成测试

使用 Espresso 测试 UI 交互。

### 无障碍服务测试

手动测试，因为需要真实的无障碍环境。

## 最佳实践

### 1. 前台服务

- ✅ 始终显示通知
- ✅ 使用合适的前台服务类型
- ✅ 及时停止不需要的服务

### 2. 无障碍服务

- ✅ 只监听必要的事件
- ✅ 及时回收 AccessibilityNodeInfo
- ✅ 避免在主线程执行耗时操作

### 3. 悬浮窗

- ✅ 检查权限后再显示
- ✅ 提供关闭悬浮窗的方式
- ✅ 悬浮窗大小适中，不遮挡重要内容

### 4. 权限

- ✅ 在使用前请求权限
- ✅ 解释为什么需要权限
- ✅ 优雅处理权限拒绝

## 扩展功能建议

### 可以添加的功能

1. **翻译功能**
   - 集成翻译 API
   - 识别文本后自动翻译
   - 显示翻译结果

2. **文本提取**
   - 提取屏幕上的所有文本
   - 导出为文本文件
   - OCR 功能增强

3. **布局可视化**
   - 显示布局层次结构
   - 高亮显示选中的控件
   - 显示控件属性

4. **自动化操作**
   - 录制用户操作
   - 回放操作序列
   - 自动化测试

## 相关资源

- [Android 开发者文档 - 无障碍服务](https://developer.android.com/guide/topics/ui/accessibility/service)
- [EasyFloat 文档](https://github.com/princekin-f/EasyFloat)
- [Android 前台服务](https://developer.android.com/guide/components/foreground-services)
- [ViewBinding 指南](https://developer.android.com/topic/libraries/view-binding)
