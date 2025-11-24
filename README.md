# 全局翻译 - Android 布局检查器

这是一个 Android 应用程序，可以通过悬浮窗检查屏幕布局并修改文字颜色，类似于 Android Studio 的 [Embedded Layout Inspector](https://developer.android.com/studio/debug/layout-inspector?utm_source=android-studio-app&utm_medium=app&utm_content=ui#embedded-layout-inspector) 功能。

## 功能特性

- ✅ 前台服务，保持应用在后台运行
- ✅ 悬浮窗功能，使用 [EasyFloat](https://github.com/princekin-f/EasyFloat) 库实现
- ✅ 无障碍服务，用于检查和访问屏幕布局
- ✅ 权限管理（通知权限、悬浮窗权限）
- ✅ 动态修改屏幕上文字颜色（从黑色到红色）

## 技术栈

- **Gradle**: 8.5
- **Android Gradle Plugin (AGP)**: 8.3.2
- **编程语言**: Kotlin
- **最小 SDK**: 24 (Android 7.0)
- **目标 SDK**: 34 (Android 14)
- **ViewBinding**: 已启用
- **AndroidX**: 已启用

## 项目结构

```
android_globalTranslate/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/globaltranslate/
│   │       │   ├── MainActivity.kt                    # 主Activity
│   │       │   └── service/
│   │       │       ├── FloatingService.kt             # 前台服务
│   │       │       └── LayoutInspectorService.kt      # 无障碍服务
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   ├── activity_main.xml              # 主界面布局
│   │       │   │   └── layout_floating_window.xml     # 悬浮窗布局
│   │       │   ├── values/
│   │       │   │   ├── strings.xml                    # 字符串资源（中文）
│   │       │   │   ├── colors.xml                     # 颜色资源
│   │       │   │   └── themes.xml                     # 主题
│   │       │   ├── drawable/
│   │       │   │   └── ic_inspect.xml                 # 检查图标
│   │       │   └── xml/
│   │       │       └── accessibility_service_config.xml # 无障碍服务配置
│   │       └── AndroidManifest.xml                    # 应用清单文件
│   └── build.gradle.kts                               # App模块构建配置
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts                                    # 项目构建配置
├── settings.gradle.kts                                 # 项目设置
├── gradle.properties                                   # Gradle属性
├── gradlew                                             # Gradle包装脚本（Unix）
└── gradlew.bat                                         # Gradle包装脚本（Windows）
```

## 依赖项

- `androidx.core:core-ktx:1.12.0`
- `androidx.appcompat:appcompat:1.6.1`
- `com.google.android.material:material:1.11.0`
- `androidx.constraintlayout:constraintlayout:2.1.4`
- `com.github.princekin-f:EasyFloat:2.0.4` - 悬浮窗库

## 如何构建

### 前提条件

1. 安装 [Android Studio](https://developer.android.com/studio)
2. 安装 JDK 8 或更高版本
3. 配置 Android SDK（API Level 34）

### 构建步骤

1. 克隆仓库：
   ```bash
   git clone https://github.com/wawo00/android_globalTranslate.git
   cd android_globalTranslate
   ```

2. 使用 Gradle 构建：
   ```bash
   ./gradlew build
   ```

3. 或者在 Android Studio 中打开项目并点击"Build" -> "Make Project"

### 生成 APK

```bash
./gradlew assembleDebug
```

生成的 APK 文件位于：`app/build/outputs/apk/debug/app-debug.apk`

## 使用说明

### 1. 安装应用

将生成的 APK 安装到 Android 设备上。

### 2. 授予权限

打开应用后，按照以下步骤操作：

1. **请求权限**：点击"请求权限"按钮
   - 授予通知权限（Android 13+）
   - 授予悬浮窗权限（Display over other apps）

2. **启用无障碍服务**：点击"启用无障碍服务"按钮
   - 在系统设置中找到"全局翻译"服务
   - 打开该服务的开关

3. **启动服务**：点击"启动服务"按钮
   - 前台服务将启动
   - 悬浮窗会显示在屏幕上

### 3. 使用功能

- 悬浮窗会以圆形按钮的形式显示在屏幕上
- 可以拖动悬浮窗到任意位置
- 点击悬浮窗按钮，应用会：
  1. 扫描当前屏幕上的所有 UI 元素
  2. 识别 TextView、Button 等文本控件
  3. 尝试将文字颜色修改为红色

### 4. 停止服务

在应用主界面点击"停止服务"按钮，前台服务和悬浮窗将被移除。

## 工作原理

### 前台服务 (FloatingService)

- 创建一个持续运行的前台服务
- 显示通知以保持服务运行
- 管理悬浮窗的生命周期
- 通过广播与无障碍服务通信

### 悬浮窗 (EasyFloat)

- 使用 EasyFloat 库创建可拖动的圆形悬浮按钮
- 点击按钮触发布局检查操作
- 发送广播通知无障碍服务执行操作

### 无障碍服务 (LayoutInspectorService)

- 监听窗口变化事件
- 接收来自前台服务的广播
- 使用 `rootInActiveWindow` 获取当前屏幕的布局树
- 递归遍历所有 UI 节点
- 识别 TextView、Button 等文本控件
- 尝试修改文本颜色（注意：由于 Android 安全限制，只能修改可编辑的控件）

## 限制和注意事项

### Android 安全限制

由于 Android 的安全机制，无障碍服务有以下限制：

1. **只能修改可编辑控件**：对于 EditText 等可编辑控件，可以使用 `ACTION_SET_TEXT` 修改文本和颜色
2. **无法修改只读控件**：对于普通的 TextView、Button 等，无法直接修改其文本颜色，因为这些是应用程序内部的视图属性
3. **应用隔离**：每个应用的 UI 是独立的，无障碍服务只能读取 UI 结构，但无法修改其他应用的 View 属性

### 类似 Embedded Layout Inspector 的功能

本应用实现了布局检查的核心功能：

- ✅ 获取屏幕布局层次结构
- ✅ 识别各类 UI 控件（TextView、Button 等）
- ✅ 读取控件文本内容
- ⚠️ 修改文字颜色（受 Android 安全限制）

要实现完整的文字颜色修改功能，需要：
- 应用具有 Root 权限，或
- 应用作为系统应用运行，或
- 在自己的应用内使用类似功能

## 权限说明

应用需要以下权限：

- `FOREGROUND_SERVICE` - 运行前台服务
- `FOREGROUND_SERVICE_SPECIAL_USE` - 特殊用途前台服务
- `POST_NOTIFICATIONS` - 发送通知（Android 13+）
- `SYSTEM_ALERT_WINDOW` - 显示悬浮窗
- `BIND_ACCESSIBILITY_SERVICE` - 绑定无障碍服务

## 开发说明

### 代码风格

- 使用 Kotlin 作为主要编程语言
- 使用 ViewBinding 进行视图绑定
- 所有按钮文本和注释使用中文
- 遵循 Android 最佳实践

### 文件编码

所有源文件使用 UTF-8 编码。

## 常见问题

### Q: 为什么无法修改其他应用的文字颜色？

A: 由于 Android 的安全机制，无障碍服务无法直接修改其他应用的 View 属性。无障碍服务主要用于辅助功能，如屏幕阅读器，而不是修改其他应用的 UI。

### Q: 如何查看检测到的控件？

A: 查看 Logcat 日志，过滤 `LayoutInspectorService` 标签，可以看到检测到的所有文本控件信息。

### Q: 悬浮窗不显示？

A: 确保已授予悬浮窗权限。在 Android 设置中，找到"特殊应用访问权限" -> "显示在其他应用上层"，确保本应用已启用。

### Q: 无障碍服务无法启用？

A: 确保在系统设置的"无障碍"中找到"全局翻译"服务并手动启用。某些设备可能需要额外的安全确认。

## 许可证

本项目仅用于学习和研究目的。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 相关链接

- [Android Embedded Layout Inspector](https://developer.android.com/studio/debug/layout-inspector?utm_source=android-studio-app&utm_medium=app&utm_content=ui#embedded-layout-inspector)
- [EasyFloat 库](https://github.com/princekin-f/EasyFloat)
- [Android 无障碍服务指南](https://developer.android.com/guide/topics/ui/accessibility/service)
