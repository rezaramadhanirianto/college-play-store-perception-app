package com.example.playstoreappperception

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.example.playstoreappperception.data.Review
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    fun callPlayStoreReview(packageName: String, reviewLimit: String, modelType: String): Deferred<Review> {
        return viewModelScope.async(Dispatchers.IO) {
            runPythonCode(packageName, reviewLimit.toInt(), modelType)
        }
    }
}