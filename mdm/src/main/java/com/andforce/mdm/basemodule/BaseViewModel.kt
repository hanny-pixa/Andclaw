package com.andforce.mdm.basemodule

import androidx.lifecycle.ViewModel
import com.andforce.mdm.basemodule.BaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {
    val TAG = javaClass.simpleName
    protected val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    protected val _failedState = MutableStateFlow(FailedBean("", Constants.INIT_ERROR_CODE, 0L))
    val failedState: StateFlow<FailedBean> = _failedState

    protected suspend fun <T> onResult(result: BaseResult<T>, block: suspend (t: T) -> Unit) {
        when (result) {
            is BaseResult.Loading -> {
                _loadingState.emit(true)
            }
            is BaseResult.Ending -> {
                _loadingState.emit(false)
            }
            is BaseResult.Failed -> {
                _failedState.emit(FailedBean(result.error, result.code, System.currentTimeMillis()))
            }
            is BaseResult.ResultData -> {
                block.invoke(result.data)
            }
        }
    }

    protected suspend fun <T> processResult(result: BaseResult<T>, onFailed: suspend (t: FailedBean) -> Unit, onSuccess: suspend (t: T) -> Unit) {
        when (result) {
            is BaseResult.Loading -> {
                _loadingState.emit(true)
            }
            is BaseResult.Ending -> {
                _loadingState.emit(false)
            }
            is BaseResult.Failed -> {
                val failedBean = FailedBean(result.error, result.code, System.currentTimeMillis())
                _failedState.emit(failedBean)
                onFailed.invoke(failedBean)
            }
            is BaseResult.ResultData -> {
                onSuccess.invoke(result.data)
            }
        }
    }
}