package com.example.playstoreappperception.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.playstoreappperception.MainActivity
import com.example.playstoreappperception.MainViewModel
import com.example.playstoreappperception.data.Review
import com.google.gson.Gson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewAnalysisScreen(viewModel: MainViewModel, navController: NavHostController) {
    val context = LocalContext.current

    var packageName by remember { mutableStateOf("") }
    var reviewLimit by remember { mutableStateOf("") }
    var predictionResults by remember { mutableStateOf<Review?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = packageName,
            onValueChange = { packageName = it },
            label = { Text("App Package (e.g., com.gojek.app)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = reviewLimit,
            onValueChange = { reviewLimit = it },
            label = { Text("Review Limit (e.g., 100)") },
            modifier = Modifier.fillMaxWidth()
        )

        val options = listOf("svm", "knn")
        var selectedOption by remember { mutableStateOf(options[0]) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Choose an option:",
                style = MaterialTheme.typography.bodyLarge
            )
            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = selectedOption == option,
                        onClick = { selectedOption = option },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        )
                    ) {
                        Text(text = option.uppercase())
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

                            val str = Gson().toJson(predictionResults)
                            navController.navigate("${MainActivity.RESULT_SCREEN}/$str")
                            loading = false
                        }
                    } else {
                        Toast.makeText(context, "Please enter both package name, review limit, and select model type", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Analyze Reviews")
            }
        }
    }
}
