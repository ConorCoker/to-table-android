package com.dining.totable.utils

object RestaurantIdToValidEmailConverter {
    fun String.toValidFirebaseAuthEmail() = "$this@totable.com"
}