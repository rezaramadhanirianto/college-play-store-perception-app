package com.example.playstoreappperception.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.playstoreappperception.MainActivity
import com.example.playstoreappperception.MainViewModel
import com.example.playstoreappperception.R
import com.example.playstoreappperception.data.AppInfo
import com.example.playstoreappperception.ui.theme.ScoreLinearGradient
import com.example.playstoreappperception.ui.theme.ScoreboardDarkGray
import com.example.playstoreappperception.ui.theme.TeslaGray
import com.example.playstoreappperception.ui.theme.TeslaWhite
import com.example.playstoreappperception.ui.theme.monserratFont
import com.google.gson.Gson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewAnalysisScreen(viewModel: MainViewModel, navController: NavHostController) {
    val context = LocalContext.current

    var packageName by remember { mutableStateOf("") }
    var reviewLimit by remember { mutableStateOf("") }
    var predictionResults by remember { mutableStateOf<AppInfo?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Google Play Store", style = TextStyle(
                    color = TeslaGray, fontSize = 20.sp, fontFamily = monserratFont
                )
            )
            Text(
                text = "Review Analysis", style = TextStyle(
                    color = TeslaWhite,
                    fontSize = 24.sp,
                    fontFamily = monserratFont,
                    fontWeight = FontWeight.Black
                )
            )
        }

        Spacer(modifier = Modifier.size(32.dp))

        Image(
            modifier = Modifier.width(200.dp),
            painter = painterResource(R.drawable.ill_analytics),
            contentDescription = null
        )

        Spacer(modifier = Modifier.size(32.dp))

        OutlinedTextField(
            value = packageName,
            onValueChange = { packageName = it },
            label = { Text("App Package (e.g., com.gojek.app)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = reviewLimit,
            onValueChange = { reviewLimit = it },
            label = { Text("Review Limit (e.g., 100)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        val options = listOf("svm", "knn")
        var selectedOption by remember { mutableStateOf(options[0]) }

        Column(modifier = Modifier.padding(16.dp)) {
            LazyRow(modifier = Modifier) {
                item {
                    Spacer(Modifier.width(28.dp))
                }
                items(options, key = { it }) {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                selectedOption = it
                            }
                            .drawBehind {
                                if (selectedOption == it)
                                    drawRoundRect(
                                        brush = ScoreLinearGradient,
                                        cornerRadius = CornerRadius(50.dp.toPx())
                                    )
                                else
                                    drawRoundRect(
                                        color = ScoreboardDarkGray,
                                        cornerRadius = CornerRadius(50.dp.toPx())
                                    )
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.uppercase(), style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = monserratFont,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Button(
                onClick = {
                    if (packageName.isNotEmpty() && reviewLimit.isNotEmpty()) {
                        coroutineScope.launch {
                            predictionResults = null
                            loading = true

                            predictionResults = viewModel.callPlayStoreReview(packageName, reviewLimit, selectedOption).await()

                            viewModel.data = predictionResults ?: return@launch

                            navController.navigate("${MainActivity.RESULT_SCREEN}")
                            loading = false
                        }
                    } else {
                        Toast.makeText(context, "Please enter both package name, review limit, and select model type", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Analyze Reviews",
                    fontSize = 16.sp,
                    fontFamily = monserratFont,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
