package com.expense.tracker

import java.util.Currency

fun getCurrencySymbol(currencyCode: String): String {
    return try {
        val currency = Currency.getInstance(currencyCode)
        currency.symbol
    } catch (e: Exception) {
        // Return a fallback symbol if the currency code is invalid
        "N/A"
    }
}