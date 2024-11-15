package com.example.playstoreappperception


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.playstoreappperception.data.AppInfo
import com.example.playstoreappperception.screen.ResultScreen
import com.example.playstoreappperception.screen.ReviewAnalysisScreen
import com.example.playstoreappperception.ui.theme.TimerDarkColor

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Chaquopy
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TimerDarkColor
                ) {
                        val navController = rememberNavController()

                        NavHost(navController = navController, startDestination = REVIEW_SCREEN) {
                            composable(REVIEW_SCREEN) { ReviewAnalysisScreen(viewModel, navController) }
                            composable("$RESULT_SCREEN") {
                                ResultScreen(navController, viewModel)
                            }
                        }
                }
            }
        }
    }

    companion object {
        const val REVIEW_SCREEN = "review_screen"
        const val RESULT_SCREEN = "result_screen"

        const val RESULT_SCREEN_PARAM_KEY = "RESULT_SCREEN_PARAM_KEY"
    }
}