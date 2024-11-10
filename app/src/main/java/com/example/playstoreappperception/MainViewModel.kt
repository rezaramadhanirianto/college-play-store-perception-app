package com.example.playstoreappperception

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playstoreappperception.data.AppInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MainViewModel: ViewModel() {
    var data = AppInfo()

    fun callPlayStoreReview(packageName: String, reviewLimit: String, modelType: String): Deferred<AppInfo> {
        return viewModelScope.async(Dispatchers.IO) {
            runPythonCode(packageName, reviewLimit.trim().toInt(), modelType)
        }
    }
}