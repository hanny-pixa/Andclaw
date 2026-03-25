# Andclaw 微信扫码绑定优化说明

## 优化内容

### 1. ClawBotAuthClient.kt 优化

#### 新增功能：
- **智能轮询机制**：动态调整轮询间隔（初始2秒，最大10秒）
- **自动重试机制**：网络异常时自动重试3次
- **完整登录流程**：`performQrLogin()` 方法封装完整扫码流程
- **状态回调**：实时返回扫码状态（等待扫码、等待确认、已确认等）

#### 关键修复：
```kotlin
// 生成新的随机UIN，避免服务器识别为同一客户端
suspend fun prepareForNewAuth(baseUrl: String, botType: String)
```

### 2. ClawBotApiClient.kt 优化

#### HTTP 客户端优化：
- **连接池复用**：减少首次连接时间
- **DNS 缓存**：加速重复请求
- **超时配置优化**：
  - 连接超时：30秒
  - 读取超时：45秒
  - 写入超时：30秒
  - 完整调用超时：60秒
- **协议支持**：优先 HTTP/2，降级到 HTTP/1.1
- **自动重连**：连接失败时自动重试

#### 唯一标识生成：
```kotlin
// 每次请求生成新的唯一UIN，解决"已有一个OpenClaw连接"问题
private fun generateUniqueUin(): String
```

## 问题解决方案

### 问题1：首次扫码提示 timeout
**原因**：网络超时配置不合理，首次连接需要较长时间
**解决**：优化超时配置，增加连接池和DNS缓存

### 问题2：微信端提示"已有一个OpenClaw连接"
**原因**：X-WECHAT-UIN 标识重复，服务器认为是同一客户端
**解决**：每次请求生成新的唯一UIN，避免状态残留

### 问题3：超时后无法重新扫码
**原因**：超时后没有清理状态，服务器保持旧连接
**解决**：新增 `prepareForNewAuth()` 方法，强制生成新标识

## 编译打包步骤

### 环境要求
- Android Studio Arctic Fox 或更高版本
- JDK 17
- Android SDK API 31+

### 编译步骤

1. **打开项目**
   ```bash
   cd Andclaw
   ```

2. **创建 local.properties**（配置 API Key）
   ```properties
   kimi_key=your_kimi_api_key
   tg_token=your_telegram_bot_token  # 可选
   ```

3. **编译 Debug 版本**
   ```bash
   ./gradlew :app:assembleDebug
   ```
   输出：`app/build/outputs/apk/debug/app-debug.apk`

4. **编译 Release 版本**
   ```bash
   ./gradlew :app:assembleRelease
   ```
   输出：`app/build/outputs/apk/release/app-release-unsigned.apk`

5. **签名 APK**（Release 版本需要签名）
   ```bash
   # 生成密钥库
   keytool -genkey -v -keystore andclaw.keystore -alias andclaw -keyalg RSA -keysize 2048 -validity 10000
   
   # 签名 APK
   jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore andclaw.keystore app-release-unsigned.apk andclaw
   
   # 对齐优化
   zipalign -v 4 app-release-unsigned.apk andclaw-release.apk
   ```

### 安装到设备
```bash
# 通过 ADB 安装
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 使用优化后的版本

1. **清理旧版本数据**
   - 设置 → 应用 → Andclaw → 存储 → 清除数据
   - 或卸载后重新安装

2. **首次扫码**
   - 打开应用，进入微信绑定页面
   - 等待二维码生成（优化后响应更快）
   - 使用微信扫码

3. **如果仍然超时**
   - 检查网络连接
   - 点击"重新生成二维码"
   - 优化后的版本会自动清理状态并生成新标识

## 文件变更列表

1. `app/src/main/java/com/andforce/andclaw/bridge/clawbot/ClawBotAuthClient.kt`
   - 新增智能轮询和重试机制
   - 新增 `prepareForNewAuth()` 方法
   - 新增 `performQrLogin()` 完整流程

2. `app/src/main/java/com/andforce/andclaw/bridge/clawbot/ClawBotApiClient.kt`
   - 优化 HTTP 客户端配置
   - 新增 `createOptimizedHttpClient()` 工厂方法
   - 优化 `generateUniqueUin()` 生成唯一标识

## 测试建议

1. **首次扫码测试**
   - 全新安装应用
   - 直接扫码绑定，观察是否超时

2. **重复扫码测试**
   - 首次扫码超时后
   - 点击重新生成二维码
   - 观察是否提示"已有一个连接"

3. **网络异常测试**
   - 弱网环境下扫码
   - 观察自动重试机制是否生效

## 联系支持

如有问题，请提交 Issue 到：https://github.com/andforce/Andclaw/issues
