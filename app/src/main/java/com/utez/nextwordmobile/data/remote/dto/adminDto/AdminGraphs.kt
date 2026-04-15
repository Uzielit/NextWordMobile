package com.utez.nextwordmobile.data.remote.dto.adminDto

data class MonthlyIncomeResponse(
    val month: String,
    val amount: Double
)

data class TransactionResponse(
    val transactionId: String,
    val topic: String,
    val studentName: String,
    val date: String,
    val amount: Double
)

data class FinancialReportResponse(
    val chartData: List<MonthlyIncomeResponse>,
    val recentTransactions: List<TransactionResponse>
)

data class ClassHistoryResponse(
    val id: String?,
    val topic: String,
    val studentName: String,
    val teacherName: String,
    val datetime: String,
    val status: String
)