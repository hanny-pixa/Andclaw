package com.andforce.andclaw

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ScreenRecordActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RECORD_ACTION = "screen_record_action"
        const val ACTION_START = "start_record"
        const val ACTION_STOP = "stop_record"

        @Volatile
        var lastResult: String? = null
    }

    private val screenCaptureRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            ScreenRecordService.prepareResult(result.resultCode, result.data!!)
            val intent = Intent(this, ScreenRecordService::class.java).apply {
                action = "START"
            }
            ContextCompat.startForegroundService(this, intent)
            lastResult = "录屏已启动"
        } else {
            lastResult = "用户取消了录屏授权"
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent.getStringExtra(EXTRA_RECORD_ACTION)) {
            ACTION_START -> {
                if (ScreenRecordService.isRecording) {
                    lastResult = "录屏已在进行中"
                    finish()
                    return
                }
                lastResult = null
                val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                screenCaptureRequest.launch(projectionManager.createScreenCaptureIntent())
            }
            ACTION_STOP -> {
                if (!ScreenRecordService.isRecording) {
                    lastResult = "当前没有在录屏"
                    finish()
                    return
                }
                val intent = Intent(this, ScreenRecordService::class.java).apply {
                    action = "STOP"
                }
                startService(intent)
                lastResult = "录屏已停止, 文件: ${ScreenRecordService.lastRecordedFile ?: "unknown"}"
                finish()
            }
            else -> {
                lastResult = "未知的录屏操作"
                finish()
            }
        }
    }
}
