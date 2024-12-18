package com.expense.tracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.expense.tracker.R
import com.expense.tracker.network.ExpenseModel
import com.expense.tracker.getExpenses
import com.expense.tracker.ui.CustomToolbar
import kotlin.random.Random

@Composable
fun DataVisualizationScreen(onBack: () -> Unit) {

    val context = LocalContext.current
    var barData by remember { mutableStateOf<List<BarData>>(emptyList()) }
    var allExpenses by remember { mutableStateOf<List<ExpenseModel>>(emptyList()) }
    var yAxisLabels by remember { mutableStateOf<List<String>>(emptyList()) }

    // Load expenses on initial composition
    LaunchedEffect(context) {
        allExpenses = getExpenses(context)

        // Group expenses by category and calculate total amounts
        val groupedExpenses = allExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Update barData state
        barData = groupedExpenses.entries.mapIndexed { index, entry ->
            BarData(
                point = Point(index.toFloat(), entry.value.toFloat()),
                color = Color(
                    Random.nextInt(100, 256),
                    Random.nextInt(100, 256),
                    Random.nextInt(100, 256)
                ),
                label = entry.key
            )
        }

        // Update Y-axis labels with dates
        yAxisLabels = allExpenses.map {
            it.date.take(10)
        }
    }

    // X-axis data (labels should be short, e.g., year, month)
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barData.size - 1)
        .bottomPadding(100.dp)
        .startDrawPadding(30.dp)
        .labelData { index ->
            barData[index].label.substring(
                0,
                4
            )
        }
        .build()

    // Y-axis data (show dates or categories here)
    val yAxisData = AxisData.Builder()
        .steps(allExpenses.size)
        .backgroundColor(color = colorResource(R.color.background))
        .labelData { index -> yAxisLabels.getOrNull(index) ?: "" }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            paddingBetweenBars = 15.dp,
            barWidth = 25.dp
        ),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 10.dp,
    )

    Column {
        CustomToolbar(title = "Analysis", showBackIcon = true, onBack = onBack, false) {}

        if (barData.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (barData.isNotEmpty()) {
                    BarChart(modifier = Modifier.fillMaxSize(), barChartData = barChartData)
                }
            }
        } else {
            // Show "No Data found" message if the list is empty
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "No Data found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorResource(R.color.text)
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DataVisualizationScreenPreview() {
    DataVisualizationScreen(onBack = {})
}