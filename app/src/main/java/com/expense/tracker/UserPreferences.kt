package com.expense.tracker

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.expense.tracker.network.ExpenseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Save login state in SharedPreferences
fun saveLoginState(context: Context, isLoggedIn: Boolean) {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putBoolean("is_logged_in", isLoggedIn)
        apply()
    }
}

// Retrieve login state from SharedPreferences
fun isLoggedIn(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("is_logged_in", false) // Default is false if not found
}

fun clearLoginState(context: Context) {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        remove("is_logged_in")
        apply()
    }
}

fun saveExpense(context: Context, expenseModel: ExpenseModel) {
    val sharedPref = context.getSharedPreferences("expense_preferences", Context.MODE_PRIVATE)
    val gson = Gson()
    val expensesJson = sharedPref.getString("expenses", "[]") // Default is an empty list
    val expenseListType = object : TypeToken<MutableList<ExpenseModel>>() {}.type
    val expenseList: MutableList<ExpenseModel> = gson.fromJson(expensesJson, expenseListType)

    // Add the new expense
    expenseList.add(expenseModel)

    // Save the updated list back to SharedPreferences
    with(sharedPref.edit()) {
        putString("expenses", gson.toJson(expenseList))
        apply()
    }
}

fun saveExpenses(context: Context, expenses: List<ExpenseModel>) {
    val sharedPref = context.getSharedPreferences("expense_preferences", Context.MODE_PRIVATE)
    val gson = Gson()
    val expensesJson = gson.toJson(expenses)
    with(sharedPref.edit()) {
        putString("expenses", expensesJson)
        apply()
    }
}

fun getExpenses(context: Context): List<ExpenseModel> {
    val sharedPref = context.getSharedPreferences("expense_preferences", Context.MODE_PRIVATE)
    val gson = Gson()
    val expensesJson = sharedPref.getString("expenses", "[]") // Default is an empty list
    val expenseListType = object : TypeToken<List<ExpenseModel>>() {}.type
    return gson.fromJson(expensesJson, expenseListType)
}

fun clearAllExpenses(context: Context) {
    val sharedPref = context.getSharedPreferences("expense_preferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        remove("expenses") // Remove the "expenses" entry completely
        apply() // Apply changes
    }
}

// Function to delete an expense by ID
fun deleteExpense(context: Context, expenseId: Int) {
    val expenses = getExpenses(context) // Get the current list of expenses
    val updatedExpenses = expenses.filterNot { it.id == expenseId } // Remove the expense with the given ID
    saveExpenses(context, updatedExpenses) // Save the updated list back to SharedPreferences
}

// Save Dark Mode Preference
fun saveDarkModePreference(context: Context, isDarkMode: Boolean) {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putBoolean("is_dark_mode", isDarkMode)
        apply()
    }
}

// Get Dark Mode Preference (Default to system theme if not set)
@Composable
fun getDarkModePreference(context: Context): Boolean {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    return sharedPref.getBoolean("is_dark_mode", isSystemInDarkTheme()) // Default to system theme
}

// Save the selected currency to SharedPreferences
fun saveCurrencyPreference(context: Context, currency: String) {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("selected_currency", currency)
        apply()
    }
}

// Get the saved currency from SharedPreferences
fun getCurrencyPreference(context: Context): String {
    val sharedPref = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    return sharedPref.getString("selected_currency", "USD") ?: "USD" // Default is USD if not found
}


