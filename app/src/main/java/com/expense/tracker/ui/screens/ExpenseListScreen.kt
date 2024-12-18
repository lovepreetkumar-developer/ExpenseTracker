package com.expense.tracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.expense.tracker.R
import com.expense.tracker.network.ExpenseModel
import com.expense.tracker.deleteExpense
import com.expense.tracker.getCurrencyPreference
import com.expense.tracker.getCurrencySymbol
import com.expense.tracker.getExpenses
import com.expense.tracker.network.ExchangeRateViewModel
import com.expense.tracker.ui.CustomToolbar
import java.text.DecimalFormat

@Composable
fun ExpenseList(
    onNavigateToExpenseEntry: () -> Unit,
    onNavigateToDataVisualization: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    var allExpenses by remember { mutableStateOf<List<ExpenseModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val selectedCurrency by remember { mutableStateOf(getCurrencyPreference(context)) }
    val selectedCurrencyAmount = remember { mutableDoubleStateOf(0.0) }
    val viewModel: ExchangeRateViewModel = viewModel()
    // Observe the state from the ViewModel
    val exchangeRates = viewModel.exchangeRates.value

    // Load expenses on initial composition
    LaunchedEffect(context) {
        allExpenses = getExpenses(context)
        viewModel.fetchExchangeRates()
        isLoading = false
    }

    // Function to handle expense deletion
    fun deleteExpenseAndUpdateState(expenseId: Int) {
        deleteExpense(context, expenseId)  // Delete the expense
        allExpenses = getExpenses(context)  // Refresh the expense list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.onBackground)
    ) {

        CustomToolbar(
            title = "All Expenses",
            showBackIcon = false,
            onBack = {},
            true,
            onMenuItemClick = { menuItem ->
                when (menuItem) {
                    "Add Expense" -> onNavigateToExpenseEntry()
                    "Analysis" -> onNavigateToDataVisualization()
                    "Settings" -> onNavigateToSettings()
                }
            })

        // Show "No Expenses" message if the list is empty
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = colors.primary,
                    strokeWidth = 8.dp
                )
            } else {
                // Check if the expenses list is empty
                if (allExpenses.isEmpty()) {
                    Text(
                        text = "No Expenses",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorResource(R.color.text)
                    )
                } else {

                    if (exchangeRates != null) {
                        val conversionRates = exchangeRates.conversion_rates

                        for ((currency, amount) in conversionRates) {
                            if (currency == selectedCurrency) {
                                selectedCurrencyAmount.doubleValue = amount
                            }
                        }

                        // List displaying the expenses
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(allExpenses.reversed()) { expense ->
                                ExpenseCard(
                                    selectedCurrency,
                                    selectedCurrencyAmount,
                                    expenseModel = expense,
                                    onNavigateToExpenseEntry = onNavigateToExpenseEntry,
                                    onDeleteExpense = { expenseId ->
                                        deleteExpenseAndUpdateState(
                                            expenseId
                                        )
                                    }
                                )
                            }
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun ExpenseCard(
    selectedCurrency: String,
    selectedCurrencyAmount: MutableDoubleState,
    expenseModel: ExpenseModel,
    onNavigateToExpenseEntry: () -> Unit,
    onDeleteExpense: (Int) -> Unit // Callback to delete expense){}){}){}
) {
    val formatter = DecimalFormat("#.##")
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Based on category, choose an appropriate icon
    val categoryIcon = when (expenseModel.category) {
        "Food" -> painterResource(id = R.drawable.category_food)
        "Transportation" -> painterResource(id = R.drawable.category_transportation)
        "Entertainment" -> painterResource(id = R.drawable.category_entertainment)
        else -> painterResource(id = R.drawable.category_others)
    }

    // Card with expense information
    Card(
        onClick = { onNavigateToExpenseEntry() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = categoryIcon,
                contentDescription = "Expense Image",
                tint = colors.onSurface,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 16.dp)
            )

            // Expense details
            Column(modifier = Modifier.weight(1f)) {

                val formattedAmount =
                    formatter.format(expenseModel.amount * selectedCurrencyAmount.doubleValue)
                Text(
                    text = "Amount: ${getCurrencySymbol(selectedCurrency) + formattedAmount}",
                    color = colors.onSurface
                )
                Text(
                    text = "Category: ${expenseModel.category}",
                    color = colors.onSurface
                )
                Text(
                    text = "Date: ${expenseModel.date}",
                    color = colors.onSurface
                )
            }

            IconButton(onClick = {
                showDialog = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete Expense",
                    tint = colors.primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Confirmation Dialog
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false // Close dialog if clicked outside
                    },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure you want to delete this expense?") },
                    confirmButton = {
                        TextButton(onClick = {
                            onDeleteExpense(expenseModel.id) // Call the delete function
                            showDialog = false // Close dialog
                            Toast.makeText(context, "Expense Deleted", Toast.LENGTH_SHORT)
                                .show()
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDialog = false // Close dialog if Cancel is clicked
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExpenseList() {
    ExpenseList(
        onNavigateToExpenseEntry = {},
        onNavigateToDataVisualization = {},
        onNavigateToSettings = {},
    )
}
