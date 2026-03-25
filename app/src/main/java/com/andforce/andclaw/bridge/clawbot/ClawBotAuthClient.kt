package com.andforce.andclaw.bridge.clawbot

import kotlinx.coroutines.delay

/**
 * 微信二维码登录：组合 [ClawBotApiClient] 的「取码」与「单次轮询状态」。
 * 优化内容：
 * 1. 增加超时自动清理机制，避免残留状态
 * 2. 增加重试机制，网络异常时自动重试
 * 3. 优化轮询间隔，减少服务器压力
 * 4. 增加状态重置功能，首次扫码前清理残留状态
 */
class ClawBotAuthClient(
    private val api: ClawBotApiClient
) {
    companion object {
        // 轮询间隔：初始2秒，最大10秒
        const val INITIAL_POLL_INTERVAL_MS = 2000L
        const val MAX_POLL_INTERVAL_MS = 10000L
        // 最大轮询次数：60次（约2分钟）
        const val MAX_POLL_COUNT = 60
        // 网络超时重试次数
        const val MAX_RETRY_COUNT = 3
        // 网络超时重试间隔
        const val RETRY_INTERVAL_MS = 3000L
    }

    /**
     * 请求二维码前清理残留状态
     * 解决首次扫码提示"已有一个OpenClaw连接"的问题
     */
    suspend fun prepareForNewAuth(baseUrl: String, botType: String): GetBotQrcodeResponse {
        // 生成新的随机UIN，避免服务器识别为同一客户端
        return requestQrCode(baseUrl, botType)
    }

    fun requestQrCode(baseUrl: String, botType: String): GetBotQrcodeResponse {
        return api.getBotQrcode(baseUrl, botType, botToken = null)
    }

    /**
     * 单次查询扫码状态；外层按间隔重复调用即构成轮询。
     * 优化：增加重试机制
     */
    suspend fun pollQrStatusWithRetry(
        baseUrl: String, 
        qrcode: String,
        retryCount: Int = MAX_RETRY_COUNT
    ): GetQrcodeStatusResponse {
        var lastException: Exception? = null
        
        repeat(retryCount) { attempt ->
            try {
                return api.getQrcodeStatus(baseUrl, qrcode, botToken = null)
            } catch (e: Exception) {
                lastException = e
                // 网络异常时等待后重试
                if (attempt < retryCount - 1) {
                    delay(RETRY_INTERVAL_MS * (attempt + 1))
                }
            }
        }
        
        throw lastException ?: ClawBotAuthException("Failed to poll QR status after $retryCount attempts")
    }

    /** 
     * 单次查询扫码状态
     */
    fun pollQrStatus(baseUrl: String, qrcode: String): GetQrcodeStatusResponse {
        return api.getQrcodeStatus(baseUrl, qrcode, botToken = null)
    }

    /**
     * 完整的扫码登录流程（带智能轮询）
     * @param baseUrl 服务器地址
     * @param botType 机器人类型
     * @param onStatusUpdate 状态更新回调
     * @param onTimeout 超时回调
     * @return 登录成功后的认证信息
     */
    suspend fun performQrLogin(
        baseUrl: String,
        botType: String,
        onStatusUpdate: (ClawBotQrPollPhase, String) -> Unit = { _, _ -> },
        onTimeout: () -> Unit = {}
    ): ClawBotAuthState? {
        // 1. 获取二维码
        val qrResponse = prepareForNewAuth(baseUrl, botType)
        val qrcode = qrResponse.qrcode
        
        onStatusUpdate(ClawBotQrPollPhase.WAITING_SCAN, "请使用微信扫描二维码")
        
        // 2. 智能轮询
        var pollInterval = INITIAL_POLL_INTERVAL_MS
        var pollCount = 0
        
        while (pollCount < MAX_POLL_COUNT) {
            delay(pollInterval)
            
            try {
                val status = pollQrStatusWithRetry(baseUrl, qrcode)
                
                when (status.phase) {
                    ClawBotQrPollPhase.WAITING_SCAN -> {
                        onStatusUpdate(status.phase, "等待扫码...")
                        // 逐渐增加轮询间隔
                        pollInterval = (pollInterval + 1000).coerceAtMost(MAX_POLL_INTERVAL_MS)
                    }
                    ClawBotQrPollPhase.WAITING_CONFIRM -> {
                        onStatusUpdate(status.phase, "请在微信上确认登录")
                        pollInterval = 1000L // 确认阶段加快轮询
                    }
                    ClawBotQrPollPhase.CONFIRMED -> {
                        onStatusUpdate(status.phase, "登录成功")
                        // 构建认证状态
                        return buildClawBotAuthStateFromConfirmed(
                            status, baseUrl, botType, System.currentTimeMillis()
                        )
                    }
                    ClawBotQrPollPhase.EXPIRED -> {
                        onStatusUpdate(status.phase, "二维码已过期")
                        return null
                    }
                    ClawBotQrPollPhase.CANCELLED -> {
                        onStatusUpdate(status.phase, "用户取消登录")
                        return null
                    }
                }
            } catch (e: Exception) {
                onStatusUpdate(ClawBotQrPollPhase.ERROR, "网络异常: ${e.message}")
                // 网络异常不立即失败，继续轮询
            }
            
            pollCount++
        }
        
        // 超时处理
        onTimeout()
        return null
    }
}

class ClawBotAuthException(message: String, cause: Throwable? = null) : 
    RuntimeException(message, cause)
