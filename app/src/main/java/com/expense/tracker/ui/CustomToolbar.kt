package com.expense.tracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expense.tracker.R

@Composable
fun CustomToolbar(
    title: String,
    showBackIcon: Boolean,
    onBack: () -> Unit,
    showMenuIcon: Boolean,
    onMenuItemClick: (String) -> Unit // Callback to handle menu item click
) {
    // State to control the DropdownMenu visibility
    val expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // Height of the toolbar
            .background(color = colorResource(R.color.toolbar))
    ) {
        if (showBackIcon) {
            // Back icon
            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Title in the center
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center) // Center the title
        )

        if (showMenuIcon) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .align(Alignment.CenterEnd)
            ) {
                // Three dots menu icon aligned to the right
                IconButton(
                    onClick = { expanded.value = !expanded.value },

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more), // Replace with your 3 dot icon
                        contentDescription = "More Options",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Dropdown menu aligned to the top end (right side)
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // This will align the menu to the right side
                ) {
                    // Menu items
                    DropdownMenuItem(
                        text = { Text("Add Expense") },
                        onClick = {
                            onMenuItemClick("Add Expense") // Handle menu click
                            expanded.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Analysis") },
                        onClick = {
                            onMenuItemClick("Analysis") // Handle menu click
                            expanded.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            onMenuItemClick("Settings") // Handle menu click
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}
