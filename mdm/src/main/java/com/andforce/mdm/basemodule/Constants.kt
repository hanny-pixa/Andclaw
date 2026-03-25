package com.andforce.mdm.basemodule

object Constants {
    const val INIT_ERROR_CODE = -2
    const val RUNTIME_ERROR = -1
    const val REPEAT_LOGIN_CODE = 10000010  //其他设备登录
    const val LOGIN_EXPIRED_CODE = 10000001 // 登录过期
    const val REQUEST_INTERVAL = 60 * 60 * 1000
    const val LOADING_START = 1L
    const val LOADING_END = 2L
}