package com.group12.uistride

// File: MyValueFormatter.kt
import com.github.mikephil.charting.formatter.ValueFormatter

class MyValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return if (value == value.toInt().toFloat()) {
            value.toInt().toString()  // Jika angka bulat, tampilkan sebagai integer
        } else {
            "%.2f".format(value)  // Jika ada angka desimal, tampilkan dengan 2 angka di belakang koma
        }
    }
}
