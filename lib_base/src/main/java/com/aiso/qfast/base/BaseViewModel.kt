package com.aiso.qfast.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiso.qfast.base.config.BuildConfig
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    open val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (BuildConfig.DEBUG) {
            throw throwable
        } else {
            Timber.e( "imEnvCoroutineExceptionHandler exception \n ${throwable.stackTraceToString()}")
        }
    }

    fun viewModelLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(context = context+coroutineExceptionHandler, start = start, block = block)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}