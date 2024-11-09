package com.example.playstoreappperception

import android.os.Bundle
import androidx.navigation.NavType
import com.example.playstoreappperception.data.Review
import com.google.gson.Gson

class ReviewParamType : NavType<Review>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Review? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): Review {
        return Gson().fromJson(value, Review::class.java)
    }

    override fun put(bundle: Bundle, key: String, value: Review) {
        bundle.putParcelable(key, value)
    }
}