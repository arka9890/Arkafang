package com.openfang.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfang.api.OpenFangApi
import com.openfang.mobile.service.ProotOrchestrator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Per-Hand state in the dashboard grid.
 */
data class HandState(
    val id: String,
    val name: String,
    val status: String,
    val lastRunAt: Long?,
    val metrics: String,  // JSON-encoded; UI parses it
)

/**
 * Aggregate dashboard UI state.
 */
data class DashboardUiState(
    val daemonRunning: Boolean = false,
    val daemonVersion: String = "unknown",
    val agents: List<com.openfang.api.AgentDto> = emptyList(),
    val hands: List<HandState> = emptyList(),
    val logTail: String = "",
    val kernelWarning: String? = null,
    val loadError: String? = null,
)

/**
 * Drives the dashboard. Polls the orchestrator for daemon status and
 * the API for hands/agents; exposes the result as [DashboardUiState].
 *
 * Review Focus #4 is wired here: when [orchestrator.status()] flips
 * from Running to Stopped unexpectedly, [DashboardUiState.daemonRunning]
 * becomes false and the UI shows a "tap Start to restart" banner.
 */
class DashboardVm(
    private val api: OpenFangApi,
    private val orchestrator: ProotOrchestrator,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        _state.update { it.copy(kernelWarning = orchestrator.checkKernelVersion()) }
        refresh()
    }

    fun refresh() {
        val st = orchestrator.status()
        _state.update {
            it.copy(
                daemonRunning = st is ProotOrchestrator.DaemonStatus.Running,
                daemonVersion = (st as? ProotOrchestrator.DaemonStatus.Running)?.version ?: "stopped",
            )
        }
        viewModelScope.launch {
            try {
                val hands = api.hands()
                val agents = api.agents()
                _state.update {
                    it.copy(
                        agents = agents,
                        hands = hands.map { h ->
                            HandState(h.id, h.name, h.status, h.lastRunAt, h.metrics)
                        },
                        loadError = null,
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loadError = e.message ?: "failed to load") }
            }
        }
    }

    fun startDaemon() {
        val r = orchestrator.start()
        _state.update {
            it.copy(
                loadError = if (r is com.openfang.proot.ProotResult.Failure) r.message else null,
            )
        }
        refresh()
    }

    fun stopDaemon() {
        orchestrator.stop()
        refresh()
    }

    fun activateHand(id: String) {
        viewModelScope.launch {
            val h = api.activateHand(id)
            _state.update { s ->
                s.copy(hands = s.hands.map { if (it.id == id) HandState(h.id, h.name, h.status, h.lastRunAt, h.metrics) else it })
            }
        }
    }

    fun pauseHand(id: String) {
        viewModelScope.launch {
            val h = api.pauseHand(id)
            _state.update { s ->
                s.copy(hands = s.hands.map { if (it.id == id) HandState(h.id, h.name, h.status, h.lastRunAt, h.metrics) else it })
            }
        }
    }

    /**
     * Parse a [HandState.metrics] JSON string into a list of
     * `key: value` pairs for display. Returns empty list on parse
     * failure (defensive — a malformed metric should not crash the UI).
     */
    fun parseMetrics(metricsJson: String): List<Pair<String, String>> {
        return try {
            val obj = Json.parseToJsonElement(metricsJson) as? JsonObject ?: return emptyList()
            obj.entries.map { (k, v) ->
                val valueStr = if (v is JsonPrimitive) v.content.toString() else v.toString()
                k to valueStr
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
