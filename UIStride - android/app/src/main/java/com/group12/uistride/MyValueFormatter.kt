package com.group12.uistride

import com.github.mikephil.charting.formatter.ValueFormatter

class MyValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value == value.toInt().toFloat()) {
            value.toInt().toString()
        } else {
            "%.2f".format(value)
        }
    }
}
