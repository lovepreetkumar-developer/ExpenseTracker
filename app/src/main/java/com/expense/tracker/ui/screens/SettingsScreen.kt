package com.expense.tracker.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.expense.tracker.R
import com.expense.tracker.clearAllExpenses
import com.expense.tracker.getCurrencyPreference
import com.expense.tracker.saveCurrencyPreference
import com.expense.tracker.saveLoginState
import com.expense.tracker.ui.CustomToolbar
import com.expense.tracker.ui.ScreenRoutes
import getThemePreference
import kotlinx.coroutines.launch
import saveThemePreference

@Composable
fun SettingsScreen(navController: NavController, onBack: () -> Unit, context: Context) {
    val colors = MaterialTheme.colorScheme
    var selectedCurrency by remember { mutableStateOf(getCurrencyPreference(context)) }
    val currencies = listOf("USD", "EUR", "GBP")
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    // Collecting the theme preference from DataStore
    val isDarkThemeFlow = getThemePreference(context).collectAsState(initial = false)
    val isDarkTheme = isDarkThemeFlow.value

    // A CoroutineScope to launch coroutines
    val coroutineScope = rememberCoroutineScope()

    // Track the switch state locally, so it updates visually
    var switchChecked by remember { mutableStateOf(isDarkTheme) }

    // This will update the switchChecked state when the theme preference changes
    LaunchedEffect(isDarkTheme) {
        switchChecked = isDarkTheme
    }

    Column {
        CustomToolbar(title = "Settings", showBackIcon = true, onBack = onBack, false) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Dark Mode Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.primary, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        // Toggle the theme and launch a coroutine to save it
                        switchChecked = !switchChecked
                        val newTheme = switchChecked
                        coroutineScope.launch {
                            saveThemePreference(context, newTheme)
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Dark Mode", modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        color = colors.onSurface
                    )
                    Switch(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        checked = switchChecked, // Link to the local state
                        onCheckedChange = { checked ->
                            switchChecked = checked
                            coroutineScope.launch {
                                saveThemePreference(context, checked)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Currency Dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopStart)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.primary, RoundedCornerShape(8.dp))
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
                            text = selectedCurrency,
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
                    currencies.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedCurrency = option
                                expanded = false
                                // Save the selected currency in SharedPreferences
                                saveCurrencyPreference(context, selectedCurrency)
                                Toast.makeText(context, "Currency value saved", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Settings Button
            Button(
                onClick = {
                    onBack()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.toolbar)
                )
            ) {
                Text("Save Currency", color = Color.White, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logout Button
            Button(
                onClick = {
                    // Update login state
                    saveLoginState(context, false)

                    // Navigate to Login screen
                    navController.navigate(ScreenRoutes.Login) {
                        popUpTo(ScreenRoutes.ExpenseList) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.toolbar)
                )
            ) {
                Text("Logout", color = Color.White, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reset Data Button
            Button(
                onClick = {
                    clearAllExpenses(context)
                    Toast.makeText(context, "All Data has been reset", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(300.dp)
            ) {
                Text("Reset All Data", color = Color.White, textAlign = TextAlign.Center)
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController(), onBack = {}, LocalContext.current)
}
