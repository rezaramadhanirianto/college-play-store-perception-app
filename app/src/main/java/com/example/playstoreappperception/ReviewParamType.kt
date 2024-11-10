package com.example.playstoreappperception

import android.os.Bundle
import androidx.navigation.NavType
import com.example.playstoreappperception.data.AppInfo
import com.google.gson.Gson

class ReviewParamType : NavType<AppInfo>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): AppInfo? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): AppInfo {
        return Gson().fromJson(value, AppInfo::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: AppInfo) {
        bundle.putParcelable(key, value)
    }
}