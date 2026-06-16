package com.example.wakati_kotlin.model

data class DashboardResponse(

    val code: Int = 0,
    val message: String? = null,

    val super_dealers: Summary? = null,
    val dealers: Summary? = null,
    val partner_agents: Summary? = null,
    val front_desk: Summary? = null,

    val partner_agent_super_dealers: Summary? = null,
    val partner_agent_dealers: Summary? = null,

    val cash_summary: CashSummary? = null
) {

    data class Summary(
        val total: Int = 0,
        val active: Int = 0,
        val inactive: Int = 0
    )

    data class CashSummary(
        val current_balance: Double = 0.0,
        val payments_collected: Double = 0.0,
        val payments_made: Double = 0.0,
        val wallet_balance: Double = 0.0
    )
}