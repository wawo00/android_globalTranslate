# 项目文件总览

本文档提供项目所有文件的快速参考。

## 📁 项目结构

```
android_globalTranslate/
├── 📄 README.md                    # 项目概述和快速开始指南
├── 📄 USAGE.md                     # 详细使用说明和故障排除
├── 📄 BUILD.md                     # 构建说明和CI/CD配置
├── 📄 IMPLEMENTATION.md            # 技术实现细节文档
├── 📄 build.gradle.kts             # 项目级Gradle配置
├── 📄 settings.gradle.kts          # Gradle设置文件
├── 📄 gradle.properties            # Gradle属性配置
├── 📄 gradlew                      # Gradle包装脚本(Unix)
├── 📄 gradlew.bat                  # Gradle包装脚本(Windows)
│
├── 📂 gradle/wrapper/
│   ├── gradle-wrapper.jar         # Gradle包装器
│   └── gradle-wrapper.properties  # Gradle版本配置
│
└── 📂 app/
    ├── 📄 build.gradle.kts        # 应用模块构建配置
    ├── 📄 proguard-rules.pro      # ProGuard混淆规则
    │
    └── 📂 src/main/
        ├── 📄 AndroidManifest.xml  # 应用清单文件
        │
        ├── 📂 java/com/example/globaltranslate/
        │   ├── 📄 MainActivity.kt  # 主Activity
        │   │
        │   └── 📂 service/
        │       ├── 📄 FloatingService.kt          # 前台服务
        │       └── 📄 LayoutInspectorService.kt   # 无障碍服务
        │
        └── 📂 res/
            ├── 📂 drawable/
            │   └── ic_inspect.xml  # 检查图标
            │
            ├── 📂 layout/
            │   ├── activity_main.xml              # 主界面布局
            │   └── layout_floating_window.xml     # 悬浮窗布局
            │
            ├── 📂 mipmap-*/
            │   ├── ic_launcher.xml                # 应用图标
            │   └── ic_launcher_round.xml          # 圆形应用图标
            │
            ├── 📂 values/
            │   ├── strings.xml     # 字符串资源(中文)
            │   ├── colors.xml      # 颜色资源
            │   └── themes.xml      # 应用主题
            │
            └── 📂 xml/
                ├── accessibility_service_config.xml  # 无障碍服务配置
                ├── backup_rules.xml                  # 备份规则
                └── data_extraction_rules.xml         # 数据提取规则
```

## 📚 文档说明

### README.md
**目标读者**: 所有用户  
**内容**:
- 项目简介和功能特性
- 技术栈说明
- 快速开始指南
- 项目结构概览
- 依赖项列表
- 构建步骤
- 使用说明
- 工作原理
- 限制说明
- 常见问题

**适用场景**: 初次接触项目，想快速了解项目是什么

### USAGE.md
**目标读者**: 终端用户  
**内容**:
- 详细的安装步骤
- 权限配置指南
- 悬浮窗使用说明
- 查看操作结果的方法
- 高级使用技巧
- 调试方法
- 常见问题解决方案
- 兼容性说明
- 反馈渠道

**适用场景**: 已安装应用，需要了解如何使用

### BUILD.md
**目标读者**: 开发者  
**内容**:
- 环境要求
- 使用Android Studio构建
- 使用命令行构建
- 输出位置说明
- 常见构建问题和解决方案
- 签名配置
- CI/CD集成示例
- 性能优化建议
- 调试构建技巧
- 版本管理

**适用场景**: 需要从源码构建应用

### IMPLEMENTATION.md
**目标读者**: 开发者和贡献者  
**内容**:
- 架构设计说明
- 各组件详细实现
- 代码片段和解释
- 数据流图
- 权限处理流程
- ViewBinding使用
- 性能优化技巧
- 调试方法
- 最佳实践
- 扩展功能建议

**适用场景**: 需要理解代码实现或贡献代码

## 📝 核心源文件

### MainActivity.kt
**行数**: ~180  
**职责**:
- 应用入口
- 权限请求管理
- 服务启动/停止控制
- UI交互处理

**关键方法**:
- `requestPermissions()` - 请求运行时权限
- `checkPermissions()` - 检查权限状态
- `openAccessibilitySettings()` - 打开无障碍设置
- `startFloatingService()` - 启动前台服务
- `stopFloatingService()` - 停止前台服务

### FloatingService.kt
**行数**: ~130  
**职责**:
- 前台服务实现
- 通知管理
- 悬浮窗生命周期
- 广播通信

**关键方法**:
- `onCreate()` - 服务初始化
- `onStartCommand()` - 启动前台服务
- `createNotification()` - 创建通知
- `showFloatingWindow()` - 显示悬浮窗
- `onFloatingButtonClicked()` - 处理点击事件

### LayoutInspectorService.kt
**行数**: ~200  
**职责**:
- 无障碍服务实现
- 布局树遍历
- UI元素识别
- 文本颜色修改尝试

**关键方法**:
- `onServiceConnected()` - 服务连接
- `onAccessibilityEvent()` - 处理无障碍事件
- `changeTextColorToRed()` - 修改文字颜色
- `traverseAndChangeTextColor()` - 遍历节点树
- `changeNodeTextColor()` - 修改单个节点

## 🎨 资源文件

### 布局文件

#### activity_main.xml
- 主界面布局
- 包含4个按钮和状态文本
- 使用ConstraintLayout

#### layout_floating_window.xml
- 悬浮窗布局
- 圆形设计，60dp×60dp
- 包含检查图标

### 字符串资源 (strings.xml)
**总数**: 16个字符串  
**语言**: 中文  
包含所有UI文本和提示消息

### 颜色资源 (colors.xml)
定义的颜色:
- `purple_200`, `purple_500`, `purple_700` - 主题色
- `teal_200`, `teal_700` - 强调色
- `black`, `white` - 基础色
- `red` - 用于修改文字颜色

### 图标资源 (drawable/)
- `ic_inspect.xml` - 信息图标，用于悬浮窗和通知

## ⚙️ 配置文件

### build.gradle.kts (项目级)
- 定义构建脚本仓库
- 配置Android Gradle Plugin版本
- 配置Kotlin插件版本

### build.gradle.kts (应用级)
- 应用ID和版本配置
- SDK版本配置
- 依赖项声明
- ViewBinding启用

### gradle.properties
重要配置:
- `android.useAndroidX=true` - 启用AndroidX
- `kotlin.code.style=official` - Kotlin代码风格
- `org.gradle.jvmargs` - JVM内存设置

### AndroidManifest.xml
声明的组件:
- 1个Activity (MainActivity)
- 2个Service (FloatingService, LayoutInspectorService)

请求的权限:
- FOREGROUND_SERVICE
- FOREGROUND_SERVICE_SPECIAL_USE
- POST_NOTIFICATIONS
- SYSTEM_ALERT_WINDOW
- BIND_ACCESSIBILITY_SERVICE

## 📊 代码统计

### 代码行数统计

| 文件 | 代码行数 | 注释行数 | 空行数 |
|------|---------|---------|--------|
| MainActivity.kt | ~150 | ~30 | ~20 |
| FloatingService.kt | ~100 | ~20 | ~15 |
| LayoutInspectorService.kt | ~150 | ~40 | ~25 |
| **总计** | **~400** | **~90** | **~60** |

### 资源文件统计

| 类型 | 数量 |
|------|------|
| Kotlin文件 | 3 |
| 布局文件 | 2 |
| XML配置 | 3 |
| 字符串资源 | 16 |
| 颜色资源 | 8 |
| 图标资源 | 11 |

## 🔧 依赖项

### 直接依赖

| 依赖项 | 版本 | 用途 |
|--------|------|------|
| androidx.core:core-ktx | 1.12.0 | AndroidX核心功能 |
| androidx.appcompat:appcompat | 1.6.1 | 兼容性支持 |
| material | 1.11.0 | Material Design |
| constraintlayout | 2.1.4 | 约束布局 |
| EasyFloat | 2.0.4 | 悬浮窗实现 |

### 传递依赖

通过上述直接依赖，项目还会自动包含约30+个传递依赖。

## 🚀 构建产物

### Debug构建
- **APK大小**: ~5-8 MB
- **位置**: `app/build/outputs/apk/debug/`
- **签名**: Debug密钥

### Release构建
- **APK大小**: ~3-5 MB (启用混淆和资源压缩)
- **位置**: `app/build/outputs/apk/release/`
- **签名**: 需要配置发布密钥

## 📖 阅读顺序建议

### 快速了解 (5分钟)
1. README.md - 项目概述

### 用户角度 (15分钟)
1. README.md - 项目概述
2. USAGE.md - 使用说明

### 开发者角度 (30分钟)
1. README.md - 项目概述
2. BUILD.md - 构建说明
3. IMPLEMENTATION.md - 实现细节
4. 浏览源代码

### 贡献者角度 (60分钟)
1. 所有文档
2. 详细阅读源代码
3. 运行和测试应用
4. 尝试修改和扩展功能

## 🔍 查找文件技巧

### 按功能查找

- **权限相关**: AndroidManifest.xml, MainActivity.kt
- **前台服务**: FloatingService.kt
- **悬浮窗**: FloatingService.kt, layout_floating_window.xml
- **无障碍**: LayoutInspectorService.kt, accessibility_service_config.xml
- **UI界面**: activity_main.xml, MainActivity.kt
- **配置**: build.gradle.kts, gradle.properties
- **文档**: *.md 文件

### 按类型查找

```bash
# 查找所有Kotlin文件
find . -name "*.kt"

# 查找所有布局文件
find . -name "*.xml" -path "*/layout/*"

# 查找所有资源文件
find . -path "*/res/*" -name "*.xml"

# 查找所有文档
find . -name "*.md"
```

## 🌟 重要文件标记

### ⭐⭐⭐ 必读文件
- README.md
- MainActivity.kt
- FloatingService.kt
- LayoutInspectorService.kt

### ⭐⭐ 推荐阅读
- USAGE.md
- BUILD.md
- AndroidManifest.xml
- build.gradle.kts

### ⭐ 根据需要阅读
- IMPLEMENTATION.md
- ProGuard规则
- 资源文件

## 📞 获取帮助

如果找不到需要的信息：
1. 搜索相关文档中的关键词
2. 查看IMPLEMENTATION.md中的详细说明
3. 在GitHub上提交Issue
4. 查阅Android官方文档

## 📅 文件更新日志

### 2024-11-24
- ✅ 创建所有核心源文件
- ✅ 创建所有资源文件
- ✅ 创建所有配置文件
- ✅ 创建完整文档

## 🔗 相关链接

- 项目仓库: https://github.com/wawo00/android_globalTranslate
- Android开发者文档: https://developer.android.com
- EasyFloat库: https://github.com/princekin-f/EasyFloat
- Kotlin文档: https://kotlinlang.org

---

**提示**: 此文件是自动生成的项目文件索引，如有更新请同步修改。
