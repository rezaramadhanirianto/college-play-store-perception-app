package com.example.playstoreappperception.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppInfo (
    var positiveReviews: Int = 0,
    var neutralReviews: Int = 0,
    var negativeReviews: Int = 0,
    var appName: String = "",
    var iconUrl: String = ""
): Parcelable