package com.omouravictor.ratesnow.model

import java.util.*
import kotlin.math.round

class Conversion(
    val fromCurrency: String,
    val toCurrency: String,
    private val amount: Float,
    private val rate: Double,
    val rateDate: Date
) {
    fun getValue() = round(amount * rate * 100) / 100
}