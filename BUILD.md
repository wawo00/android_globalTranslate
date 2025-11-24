# 构建说明

## 环境要求

### 必需软件

1. **JDK (Java Development Kit)**
   - 版本: JDK 8 或更高
   - 推荐: JDK 17
   - 下载: https://adoptium.net/

2. **Android Studio**
   - 版本: Android Studio Giraffe (2022.3.1) 或更高
   - 推荐: Android Studio Hedgehog (2023.1.1) 或更新版本
   - 下载: https://developer.android.com/studio

3. **Android SDK**
   - 最低 SDK: API 24 (Android 7.0)
   - 目标 SDK: API 34 (Android 14)
   - Android Studio 会自动下载 SDK

### Gradle 版本

- Gradle: 8.5
- Android Gradle Plugin (AGP): 8.3.2

## 构建步骤

### 方法 1: 使用 Android Studio（推荐）

这是最简单的方法，适合大多数用户。

#### 步骤 1: 导入项目

1. 打开 Android Studio
2. 选择 "File" -> "Open"
3. 导航到项目根目录并选择它
4. 点击 "OK"

#### 步骤 2: 同步 Gradle

Android Studio 会自动开始同步 Gradle：
- 首次导入可能需要几分钟
- 等待 "Gradle sync" 完成
- 如果出现提示，点击 "Sync Now"

#### 步骤 3: 构建项目

1. 点击菜单 "Build" -> "Make Project"
2. 或使用快捷键: `Ctrl+F9` (Windows/Linux) 或 `Cmd+F9` (Mac)
3. 等待构建完成

#### 步骤 4: 生成 APK

有两种方式：

**Debug APK (用于测试)**
1. 点击 "Build" -> "Build Bundle(s) / APK(s)" -> "Build APK(s)"
2. 构建完成后，点击通知中的 "locate" 查看 APK
3. APK 位置: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK (用于发布)**
1. 点击 "Build" -> "Generate Signed Bundle / APK"
2. 选择 "APK"
3. 选择或创建密钥库
4. 填写密钥信息
5. 选择 "release" 构建类型
6. 点击 "Finish"

### 方法 2: 使用命令行

适合自动化构建或 CI/CD 流程。

#### Windows

```cmd
# 构建 Debug APK
gradlew.bat assembleDebug

# 构建 Release APK (未签名)
gradlew.bat assembleRelease

# 运行所有检查和构建
gradlew.bat build

# 清理构建
gradlew.bat clean
```

#### Linux/Mac

```bash
# 赋予执行权限（首次运行）
chmod +x gradlew

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK (未签名)
./gradlew assembleRelease

# 运行所有检查和构建
./gradlew build

# 清理构建
./gradlew clean
```

#### 查看所有可用任务

```bash
./gradlew tasks
```

## 输出位置

### APK 文件

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`

### AAB 文件 (Android App Bundle)

如果生成了 AAB：
- `app/build/outputs/bundle/debug/app-debug.aab`
- `app/build/outputs/bundle/release/app-release.aab`

## 常见构建问题

### 问题 1: Gradle 同步失败

**错误信息:** "Could not resolve com.android.tools.build:gradle:8.3.2"

**解决方案:**
1. 检查网络连接
2. 确保可以访问 Google Maven 仓库 (https://dl.google.com)
3. 如果在中国大陆，可能需要配置代理或使用镜像

**配置代理:**

在 `gradle.properties` 中添加：
```properties
systemProp.http.proxyHost=your_proxy_host
systemProp.http.proxyPort=your_proxy_port
systemProp.https.proxyHost=your_proxy_host
systemProp.https.proxyPort=your_proxy_port
```

**使用阿里云镜像:**

在 `build.gradle.kts` 中修改仓库配置：
```kotlin
allprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 问题 2: JDK 版本不匹配

**错误信息:** "Unsupported class file major version X"

**解决方案:**
1. 确保安装了 JDK 8 或更高版本
2. 在 Android Studio 中设置 JDK:
   - File -> Project Structure -> SDK Location
   - 设置 JDK location
3. 或设置环境变量 `JAVA_HOME`

### 问题 3: SDK 许可未接受

**错误信息:** "Failed to install the following Android SDK packages... licenses have not been accepted"

**解决方案:**

```bash
# 接受所有许可
sdkmanager --licenses

# 或在 Android Studio 中:
# Tools -> SDK Manager -> SDK Tools -> 勾选 "Show Package Details"
# 查看并接受许可
```

### 问题 4: 内存不足

**错误信息:** "Expiring Daemon because JVM heap space is exhausted"

**解决方案:**

在 `gradle.properties` 中增加内存：
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=1024m -XX:+HeapDumpOnOutOfMemoryError
```

### 问题 5: 构建缓存问题

**症状:** 构建失败但错误信息不明确

**解决方案:**

清理构建并重新构建：
```bash
./gradlew clean build --refresh-dependencies
```

或在 Android Studio 中:
- Build -> Clean Project
- Build -> Rebuild Project

### 问题 6: Kotlin 版本冲突

**错误信息:** "Kotlin version conflict"

**解决方案:**

确保所有 Kotlin 版本一致。检查：
- `build.gradle.kts` 中的 Kotlin 插件版本
- 所有依赖项的 Kotlin 版本

### 问题 7: EasyFloat 依赖无法下载

**错误信息:** "Could not resolve com.github.princekin-f:EasyFloat:2.0.4"

**解决方案:**

1. 确保 JitPack 仓库已添加：
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

2. 检查网络连接到 JitPack
3. 尝试使用 VPN 或代理

## 签名配置

### 创建签名密钥

使用 keytool 创建密钥库：

```bash
keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

### 配置签名

在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/my-release-key.keystore")
            storePassword = "your_store_password"
            keyAlias = "my-key-alias"
            keyPassword = "your_key_password"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

**注意:** 不要将密钥和密码提交到版本控制系统！

### 使用环境变量

更安全的方式是使用环境变量：

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "my-release-key.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
}
```

## CI/CD 集成

### GitHub Actions 示例

创建 `.github/workflows/build.yml`:

```yaml
name: Android CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build with Gradle
      run: ./gradlew build
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

## 性能优化

### 加速构建

在 `gradle.properties` 中添加：

```properties
# 启用并行构建
org.gradle.parallel=true

# 启用配置缓存
org.gradle.configuration-cache=true

# 启用构建缓存
org.gradle.caching=true

# 使用守护进程
org.gradle.daemon=true
```

### 减小 APK 大小

在 `app/build.gradle.kts` 中：

```kotlin
android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

## 调试构建

### 详细日志

```bash
./gradlew build --info
./gradlew build --debug
./gradlew build --stacktrace
```

### 查看依赖树

```bash
./gradlew app:dependencies
```

### 扫描构建

```bash
./gradlew build --scan
```

## 版本管理

### 更新版本号

在 `app/build.gradle.kts` 中：

```kotlin
android {
    defaultConfig {
        versionCode = 2  // 递增此数字
        versionName = "1.1.0"  // 更新版本名称
    }
}
```

## 支持和帮助

如果遇到其他构建问题：

1. 查看 Android Studio 的 Build 窗口中的错误信息
2. 检查 Gradle Console 输出
3. 在 GitHub Issues 中搜索类似问题
4. 提交新的 Issue 并附上完整的错误日志

## 相关资源

- [Android 开发者文档](https://developer.android.com)
- [Gradle 用户指南](https://docs.gradle.org)
- [Kotlin 文档](https://kotlinlang.org/docs/)
- [Android Gradle Plugin 版本说明](https://developer.android.com/studio/releases/gradle-plugin)
