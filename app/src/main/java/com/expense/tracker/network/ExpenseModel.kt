package com.expense.tracker.network

data class ExpenseModel(
    val id: Int = 0,
    val category: String,
    val amount: Double,
    val date: String
)
