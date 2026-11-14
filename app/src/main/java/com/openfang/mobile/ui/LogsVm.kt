package com.openfang.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfang.proot.ProotPaths
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * UI state for the logs screen.
 */
data class LogsUiState(
    val tail: String = "",
    val filterLevel: String = "all", // all | error | info
)

/**
 * Tails the OpenFang log file (and Proot stdout ring buffer) for
 * the Logs screen. Polls every 2 s.
 */
class LogsVm(private val paths: ProotPaths) : ViewModel() {

    private val _state = MutableStateFlow(LogsUiState())
    val state: StateFlow<LogsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                refresh()
                kotlinx.coroutines.delay(2000)
            }
        }
    }

    fun refresh() {
        val logFile = paths.openfangLog
        val text = if (logFile.exists()) {
            // Last 64 KB
            val buf = ByteArray(64 * 1024)
            try {
                logFile.length().let { len ->
                    val toRead = minOf(len.toLong(), buf.size.toLong()).toInt()
                    logFile.inputStream().use { ins ->
                        ins.skip(len - toRead)
                        ins.read(buf, 0, toRead)
                    }
                }
                String(buf, 0, minOf(buf.size, logFile.length().toInt()))
            } catch (_: Exception) { "" }
        } else {
            "(log file not created yet — start the daemon first)"
        }
        _state.update { it.copy(tail = text) }
    }

    fun setFilter(level: String) {
        _state.update { it.copy(filterLevel = level) }
    }
}
