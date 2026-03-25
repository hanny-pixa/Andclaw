# GitHub Actions 自动编译指南

## 配置步骤

### 1. Fork 仓库到个人 GitHub 账号

访问 https://github.com/andforce/Andclaw ，点击右上角 **Fork** 按钮。

### 2. 配置 Secrets（可选，用于签名 Release APK）

进入 Fork 后的仓库 → Settings → Secrets and variables → Actions → New repository secret

需要配置的 Secrets：

| Secret Name | 说明 | 是否必需 |
|------------|------|---------|
| `KIMI_KEY` | Kimi Code API Key | 可选 |
| `TG_TOKEN` | Telegram Bot Token | 可选 |
| `SIGNING_KEY` | APK 签名密钥（Base64编码） | Release 必需 |
| `ALIAS` | 密钥别名 | Release 必需 |
| `KEY_STORE_PASSWORD` | 密钥库密码 | Release 必需 |
| `KEY_PASSWORD` | 密钥密码 | Release 必需 |

### 3. 生成签名密钥

```bash
# 生成密钥库
keytool -genkey -v -keystore andclaw.keystore -alias andclaw -keyalg RSA -keysize 2048 -validity 10000

# 转换为 Base64
cat andclaw.keystore | base64 > signing_key_base64.txt
```

将 `signing_key_base64.txt` 内容复制到 `SIGNING_KEY` Secret。

### 4. 触发编译

#### 方式一：自动触发
- Push 代码到 main/master 分支
- 创建 Pull Request

#### 方式二：手动触发
1. 进入仓库 Actions 页面
2. 选择 "Build Andclaw APK" 工作流
3. 点击 "Run workflow"
4. 选择 Build Type（debug 或 release）
5. 点击 "Run workflow"

### 5. 下载 APK

编译完成后：
1. 进入 Actions 页面
2. 点击对应的 workflow run
3. 在 Artifacts 区域下载 APK

## 工作流说明

### build.yml - 日常编译
- **触发条件**: Push、PR、手动触发
- **输出**: Debug/Release APK（未签名）
- **用途**: 日常开发测试

### release.yml - 发布编译
- **触发条件**: Tag push、手动触发
- **输出**: 签名的 Release APK
- **用途**: 正式版本发布

## 编译状态查看

访问: `https://github.com/你的用户名/Andclaw/actions`

## 常见问题

### Q: 编译失败怎么办？
A: 检查 Actions 日志，常见问题：
- 缺少 local.properties（已自动处理）
- Gradle 缓存问题（已配置缓存）
- 代码语法错误

### Q: 如何修改编译配置？
A: 编辑 `.github/workflows/build.yml` 文件

### Q: Debug 和 Release 的区别？
A: 
- Debug: 包含调试信息，未优化，未签名
- Release: 代码优化，ProGuard 混淆，已签名

### Q: 可以不签名吗？
A: 可以，Debug 版本不需要签名。Release 版本建议签名。

## 优化后的代码说明

本次优化包含以下文件变更：

1. **ClawBotAuthClient.kt**
   - 新增智能轮询机制
   - 新增自动重试机制
   - 新增 `prepareForNewAuth()` 方法

2. **ClawBotApiClient.kt**
   - 优化 HTTP 客户端配置
   - 新增连接池和 DNS 缓存
   - 优化超时配置

3. **新增文件**
   - `.github/workflows/build.yml`
   - `.github/workflows/release.yml`
   - `OPTIMIZATION.md`
   - `GITHUB_ACTIONS_GUIDE.md`

## 下一步操作

1. Fork 仓库
2. 推送优化后的代码（已准备好）
3. 配置 Secrets（可选）
4. 触发编译
5. 下载 APK 安装测试

## 联系方式

如有问题，请提交 Issue 到：https://github.com/andforce/Andclaw/issues
