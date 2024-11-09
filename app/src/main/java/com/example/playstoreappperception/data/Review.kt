package com.example.playstoreappperception.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review (
    var positiveReviews: Int,
    var neutralReviews: Int,
    var negativeReviews: Int
): Parcelable