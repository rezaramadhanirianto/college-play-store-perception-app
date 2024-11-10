package com.example.playstoreappperception

import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.playstoreappperception.data.AppInfo

fun runPythonCode(appPackage: String, reviewLimit: Int, modelType: String): AppInfo {
    // Initialize Python
    val python = Python.getInstance()
    val pyModule = python.getModule("main")  // Use the file name without ".py"

    // Call the Python function with parameters
    val result: PyObject = pyModule.callAttr("fetch_and_analyze", appPackage, reviewLimit, modelType)

    // Manually specify the types while retrieving values from resultMap
    val resultMap = result.asMap() as Map<String, PyObject>
    val positiveReviews = resultMap["positive_reviews"]?.toInt() ?: 0
    val negativeReviews = resultMap["neutral_reviews"]?.toInt() ?: 0
    val neutralReviews = resultMap["negative_reviews"]?.toInt() ?: 0
    val appName = resultMap["title"]?.toString() ?: ""
    val iconUrl = resultMap["iconUrl"]?.toString() ?: ""

    Log.d("Util", "runPythonCode: ${resultMap}")

    // Format the results into a single string
    return AppInfo(positiveReviews, neutralReviews, negativeReviews, appName, iconUrl)
}

fun init(){
    val python = Python.getInstance()
    val pyModule = python.getModule("createmodel")  // Use the file name without ".py"
    pyModule.callAttr("init")
}