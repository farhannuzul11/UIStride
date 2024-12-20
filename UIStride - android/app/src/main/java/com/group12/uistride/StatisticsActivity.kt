package com.group12.uistride

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.group12.uistride.model.BaseResponse
import com.group12.uistride.request.BaseApiService
import com.group12.uistride.request.UtilsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatisticsActivity : AppCompatActivity() {

    private lateinit var barChartDistance: BarChart
    private lateinit var barChartSteps: BarChart
    private lateinit var spinnerPeriod: Spinner
    private lateinit var mApiService: BaseApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        supportActionBar?.hide();

        barChartDistance = findViewById(R.id.barChartDistance)
        barChartSteps = findViewById(R.id.barChartSteps)
        spinnerPeriod = findViewById(R.id.spinnerPeriod)
        mApiService = UtilsApi.getApiService()

        val accountId = intent.getLongExtra("accountId", -1L)
        if (accountId == -1L) {
            Toast.makeText(this, "Invalid Account ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup Spinner
        spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPeriod = when (position) {
                    0 -> "daily"
                    1 -> "weekly"
                    2 -> "monthly"
                    3 -> "yearly"
                    else -> "daily"
                }
                fetchStatistics(accountId, selectedPeriod)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchStatistics(accountId: Long, period: String) {
        mApiService.getGroupedStatistics(accountId, period).enqueue(object : Callback<BaseResponse<Map<String, Any>>> {
            override fun onResponse(
                call: Call<BaseResponse<Map<String, Any>>>,
                response: Response<BaseResponse<Map<String, Any>>>
            ) {
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val statistics = response.body()!!.payload
                    // Handle the statistics based on the selected period
                    val distanceStats = statistics["distanceByDay"] ?: statistics["distanceByWeek"] ?: statistics["distanceByMonth"] ?: statistics["distanceByYear"]
                    val stepsStats = statistics["stepsByDay"] ?: statistics["stepsByWeek"] ?: statistics["stepsByMonth"] ?: statistics["stepsByYear"]

                    Log.d("Statistics", "distanceStats: $distanceStats")
                    Log.d("Statistics", "stepsStats: $stepsStats")

                    setupBarChart(barChartDistance, distanceStats, "Total Distance")
                    setupBarChart(barChartSteps, stepsStats, "Total Steps")
                } else {
                    Toast.makeText(this@StatisticsActivity, "Failed to load statistics", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<Map<String, Any>>>, t: Throwable) {
                Toast.makeText(this@StatisticsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupBarChart(barChart: BarChart, stats: Any?, label: String) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        if (stats is Map<*, *>) {
            stats.forEach { (key, value) ->
                val xValue = getShortDayName(key.toString())
                val yValue = (value as? Number)?.toFloat() ?: 0f
                entries.add(BarEntry(entries.size.toFloat(), yValue))
                labels.add(xValue)
            }
        }

        // Mengatur warna berdasarkan label atau chart
        val barDataSet = BarDataSet(entries, label)
        barDataSet.colors = listOf(getColorForChart(label))  // Mengatur warna berdasarkan label

        val barData = BarData(barDataSet)
        barData.setValueTextSize(12f)

        barChart.data = barData
        barChart.description.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        barChart.animateY(1000)
        barChart.invalidate()
    }

    // Fungsi untuk mengubah nama hari menjadi singkatan 3 huruf
    private fun getShortDayName(dayName: String): String {
        val daysOfWeek = arrayOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")
        val shortNames = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

        val index = daysOfWeek.indexOf(dayName.toUpperCase())
        return if (index >= 0) shortNames[index] else dayName
    }

    // Fungsi untuk menentukan warna kuning untuk chart
    private fun getColorForChart(label: String): Int {
        return when (label) {
            "Total Distance" -> Color.parseColor("#e9724d")
            "Total Steps" -> Color.parseColor("#79ccb3")
            else -> Color.YELLOW  // Default ke warna kuning
        }
    }


}
