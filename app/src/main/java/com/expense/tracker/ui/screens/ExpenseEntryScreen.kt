package com.expense.tracker.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expense.tracker.R
import com.expense.tracker.network.ExpenseModel
import com.expense.tracker.saveExpense
import com.expense.tracker.ui.CustomToolbar
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun ExpenseEntryScreen(onBack: () -> Unit) {

    val colors = MaterialTheme.colorScheme
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Category") }
    val categories = listOf(
        "Food & Dining",
        "Transportation",
        "Entertainment",
        "Housing",
        "Healthcare",
        "Shopping",
        "Education",
        "Personal Care",
        "Debt Payments",
        "Miscellaneous"
    )
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // DatePicker dialog
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, selectedDay ->
            date = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${
                String.format(
                    "%02d",
                    selectedDay
                )
            }"
        },
        year,
        month,
        day
    )

    // Disable future dates by setting the max date to today
    val today = calendar.timeInMillis
    datePickerDialog.datePicker.maxDate = today

    Column {

        CustomToolbar(title = "Add Expense", showBackIcon = true, onBack = onBack, false) {}

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Category Dropdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                colors.primary,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                expanded = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 20.dp
                                    )
                                    .weight(1f),
                                text = category,
                                style = TextStyle(
                                    color = colors.primary,
                                    fontSize = 14.sp,
                                )
                            )
                            IconButton(
                                onClick = { expanded = true }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.down_arrow),
                                    contentDescription = "Calendar Icon",
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Field with Calendar Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (yyyy-MM-dd)") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { datePickerDialog.show() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Calendar Icon",
                            tint = colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = {
                        if (amount.isNotEmpty() && category.isNotEmpty() && date.isNotEmpty()) {
                            val newExpense = ExpenseModel(
                                id = System.currentTimeMillis().toInt(), // Use a unique ID
                                category = category,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                date = date
                            )
                            saveExpense(context = context, expenseModel = newExpense)
                            Toast.makeText(context, "Expense Added", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(
                                context,
                                "Please fill all the fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.toolbar)
                    )
                ) {
                    Text("Save Expense")
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseEntryScreenPreview() {
    ExpenseEntryScreen(onBack = {})
}
