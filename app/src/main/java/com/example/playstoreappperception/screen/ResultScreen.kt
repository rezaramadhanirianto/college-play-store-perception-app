package com.example.playstoreappperception.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.playstoreappperception.AppIconButton
import com.example.playstoreappperception.LoadImageFromUrl
import com.example.playstoreappperception.MainViewModel
import com.example.playstoreappperception.data.AppInfo
import com.example.playstoreappperception.ui.theme.TeslaWhite
import com.example.playstoreappperception.ui.theme.monserratFont
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Pie
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: MainViewModel,
) {
    val data = viewModel.data

    val positiveReviews = data.positiveReviews
    val negativeReviews = data.negativeReviews
    val neutralReviews = data.neutralReviews

    val totalReviews = positiveReviews + negativeReviews + neutralReviews
    val positivePercentage = positiveReviews / totalReviews.toFloat() * 100
    val negativePercentage = negativeReviews / totalReviews.toFloat() * 100
    val neutralPercentage = neutralReviews / totalReviews.toFloat() * 100
    val totalPercentage = 100 - positivePercentage - negativePercentage
    val scrollState = rememberScrollState()

    // Create pie chart data
    val pieChartData = listOf(
        Pie("Positif Review", data = positivePercentage.roundToInt().toDouble(), Color(0xFF0EA463)),
        Pie("Negative Review", data = negativePercentage.roundToInt().toDouble(), Color(0xFFF84B56)),
        Pie("Tidak Diketahui", data = totalPercentage.roundToInt().toDouble(), Color(0xFF292929))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Top Navigation Bar
        TopAppBar(
            title = {
                Text(
                    text = "Hasil Review",
                    fontSize = 20.sp,
                    style = TextStyle(
                        color = TeslaWhite,
                        fontSize = 24.sp,
                        fontFamily = monserratFont,
                        fontWeight = FontWeight.Black
                    )
                )
            },
            navigationIcon = {
                AppIconButton(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color.White
                ) {
                    navController.popBackStack()
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.Black
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = data.appName, textAlign = TextAlign.Center, style = TextStyle(
                    color = TeslaWhite,
                    fontSize = 24.sp,
                    fontFamily = monserratFont,
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoadImageFromUrl(data.iconUrl)

            Spacer(modifier = Modifier.height(32.dp))


            // Display Pie Chart Data
            pieChartData.forEach { data ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Box(modifier = Modifier.size(16.dp).background(data.color))
                    Text("${data.label}: ${data.data}%", color = TeslaWhite)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pie Chart
            PieChart(
                modifier = Modifier.size(200.dp),
                data = pieChartData,
                selectedScale = 1.2f,
                scaleAnimEnterSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                colorAnimEnterSpec = tween(300),
                colorAnimExitSpec = tween(300),
                scaleAnimExitSpec = tween(300),
                spaceDegreeAnimExitSpec = tween(300),
                style = Pie.Style.Fill
            )

            // Spacer
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
@Preview
private fun ResultScreen_Preview() {
    ResultScreen(
        navController = rememberNavController(),
        viewModel = MainViewModel().apply {
            data = AppInfo(positiveReviews = 10, negativeReviews = 4, neutralReviews = 3, appName = "Gojek", iconUrl = "")
        }
    )
}
