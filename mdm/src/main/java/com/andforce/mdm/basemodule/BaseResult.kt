package com.andforce.mdm.basemodule

sealed class BaseResult<T> {
    data class Loading<E>(val show: Boolean) : BaseResult<E>()
    data class Ending<E>(val show: Boolean) : BaseResult<E>()
    data class Failed<E>(val error: String, val code: Int) : BaseResult<E>()
    data class ResultData<E>(val code: Int, val message: String, val data: E) : BaseResult<E>()
}