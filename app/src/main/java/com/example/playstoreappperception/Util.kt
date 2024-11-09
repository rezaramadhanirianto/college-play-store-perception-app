package com.example.playstoreappperception

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.example.playstoreappperception.data.Review

fun runPythonCode(appPackage: String, reviewLimit: Int, modelType: String): Review {
    // Initialize Python
    val python = Python.getInstance()
    val pyModule = python.getModule("main")  // Use the file name without ".py"

    // Call the Python function with parameters
    val result: PyObject = pyModule.callAttr("analyze_reviews", appPackage, reviewLimit, modelType)

    // Manually specify the types while retrieving values from resultMap
    val resultMap = result.asMap() as Map<String, PyObject>
    val positiveReviews = resultMap["positive_reviews"]?.toInt() ?: 0
    val negativeReviews = resultMap["neutral_reviews"]?.toInt() ?: 0
    val neutralReviews = resultMap["negative_reviews"]?.toInt() ?: 0

    // Format the results into a single string
    return Review(positiveReviews, neutralReviews, negativeReviews)
}

fun init(){
    val python = Python.getInstance()
    val pyModule = python.getModule("createmodel")  // Use the file name without ".py"
    pyModule.callAttr("init")
}