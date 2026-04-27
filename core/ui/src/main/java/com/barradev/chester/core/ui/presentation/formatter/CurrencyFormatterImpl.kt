package com.barradev.chester.core.ui.presentation.formatter

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class CurrencyFormatterImpl @Inject constructor(): CurrencyFormatter {

    private val argentinaLocale = Locale.forLanguageTag("es-AR")

    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(argentinaLocale).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }


    override fun format(amount: Double): String {
        return formatter.format(amount)
    }
}