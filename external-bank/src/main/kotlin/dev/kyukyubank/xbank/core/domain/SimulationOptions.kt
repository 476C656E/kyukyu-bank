package dev.kyukyubank.xbank.core.domain

data class SimulationOptions(
    val failureRate: Int? = null, // 0~100
    val delayMs: Long? = null     // ms
)
