package com.group12.uistride

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.widget.Toast

class RecordActivity : AppCompatActivity(), SensorEventListener{
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var totalDistance = 0.0
    private var lastLocation: Location? = null
    private lateinit var distanceTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var totalSteps = 0
    private var currentSteps = 0
    private val locationPermissionCode = 101
    private val REQUEST_CHECK_SETTINGS = 102
    private val REQUEST_ACTIVITY_RECOGNITION = 103

    // Inisialisasi Polyline
    private lateinit var pathOverlay: Polyline
    private var currentMarker: Marker? = null // Menyimpan referensi marker saat ini
    private lateinit var startButton: Button // Tambahkan variabel untuk tombol Start
    private var isTracking = false // Variabel untuk memantau apakah tracking aktif
    private var isMapCentered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi osmdroid
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        Configuration.getInstance().load(applicationContext, sharedPreferences)

        setContentView(R.layout.activity_record)

        // Setup mapView
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Inisialisasi TextView untuk menampilkan jarak
        distanceTextView = findViewById(R.id.distanceTextView)
        stepsTextView = findViewById(R.id.stepsTextView)

        // Inisialisasi Polyline untuk menggambar rute
        pathOverlay = Polyline()
        pathOverlay.outlinePaint.color = ContextCompat.getColor(this, R.color.path_orange) // Ganti dengan warna yang diinginkan
        pathOverlay.outlinePaint.strokeWidth = 8.0f // Lebar garis
        mapView.overlays.add(pathOverlay) // Menambahkan Polyline ke peta

        // Inisialisasi FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Membuat permintaan lokasi
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 detik
            fastestInterval = 5000 // 5 detik
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Mengecek izin lokasi dan memeriksa status GPS
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        } else {
            checkGpsStatus() // Mengecek status GPS jika izin diberikan
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 atau lebih tinggi
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                // Permintaan izin jika belum diberikan
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    REQUEST_ACTIVITY_RECOGNITION
                )
            }
        }

        // Inisialisasi tombol Start
        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            if (!isTracking) {
                startTracking()  // Mulai perhitungan saat tombol "Start" ditekan
            } else {
                stopTracking()  // Hentikan perhitungan saat tombol "Stop" ditekan
            }
        }

        // Inisialisasi sensor langkah
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            stepsTextView.text = "Step counter sensor not available."
        }
    }

    private fun startTracking() {
        isTracking = true
        totalDistance = 0.0
        distanceTextView.text = "Distance: 0.00 km"
        pathOverlay.points.clear()
        totalSteps = 0
        currentSteps = -1 // Inisialisasi ke -1 untuk menandakan bahwa belum diatur
        stepsTextView.text = "Steps: 0"
        startButton.text = "Stop"
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.stop_red))

        // Mulai mendapatkan lokasi pengguna
        getUserLocation()
    }

    private fun stopTracking() {
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)  // Hentikan pembaruan sensor langkah
        startButton.text = "Start"
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER && isTracking) {
            if (currentSteps == -1) {
                // Set langkah awal sebagai nilai pertama yang didapat dari sensor
                currentSteps = event.values[0].toInt()
            }
            // Hitung langkah sejak tracking dimulai
            totalSteps = event.values[0].toInt() - currentSteps
            stepsTextView.text = "Steps: $totalSteps"
        }
    }

    override fun onResume() {
        super.onResume()
        // Daftarkan listener sensor saat Activity aktif kembali jika tracking aktif
        if (isTracking) {
            stepCounterSensor?.also { sensor ->
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Batalkan pendaftaran listener sensor saat Activity dijeda
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak perlu tindakan khusus saat akurasi berubah untuk step counter
    }

    // Fungsi untuk memeriksa apakah GPS aktif
    private fun checkGpsStatus() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true) // Mengatur agar dialog muncul jika GPS tidak aktif

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Jika GPS aktif, ambil lokasi pengguna
            getUserLocation()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // GPS tidak aktif, minta pengguna untuk mengaktifkan
                try {
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Handle the error
                }
            }
        }
    }

    // Menangani hasil dari permintaan pengguna untuk mengaktifkan GPS
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // GPS diaktifkan oleh pengguna, ambil lokasi pengguna
                getUserLocation()
            }
        }
    }

    // Fungsi untuk mendapatkan lokasi pengguna
    private fun getUserLocation() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isTracking) { // Memastikan hanya melakukan update jika tracking aktif
                    for (location in locationResult.locations) {
                        updateLocation(location) // Memanggil fungsi updateLocation
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    // Fungsi untuk memperbarui lokasi dan menghitung jarak
    private fun updateLocation(location: Location) {
        if (!isTracking) return

        val latitude = location.latitude
        val longitude = location.longitude
        val newPoint = GeoPoint(latitude, longitude)

        val mapController = mapView.controller
        if (!isMapCentered) {
            // Hanya pusatkan peta sekali, ketika pertama kali lokasi diterima
            mapController.setZoom(15.0)
            mapController.setCenter(newPoint)
            isMapCentered = true
        }

        // Hapus marker sebelumnya
        currentMarker?.let {
            mapView.overlays.remove(it)
        }

        // Tambahkan marker baru
        currentMarker = Marker(mapView).apply {
            position = newPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(currentMarker!!)

        if (lastLocation != null) {
            val distance = lastLocation!!.distanceTo(location)
            totalDistance += distance / 1000.0
            distanceTextView.text = "Distance: %.2f km".format(totalDistance)
        }

        // Tambahkan titik ke Polyline dan perbarui lastLocation
        pathOverlay.addPoint(newPoint)
        lastLocation = location
    }

    // Menangani permintaan izin lokasi
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsStatus() // Memeriksa status GPS setelah izin lokasi diberikan
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_ACTIVITY_RECOGNITION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Activity recognition permission granted",
                        Toast.LENGTH_SHORT
                    ).show()
                    stepCounterSensor?.let {
                        sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
                    }
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
                        // Pengguna menolak izin, tetapi tanpa memilih "Don't ask again"
                        Toast.makeText(
                            this,
                            "Activity recognition permission denied. Please enable it for proper functionality.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Pengguna menolak izin dengan "Don't ask again"
                        Toast.makeText(
                            this,
                            "Permission permanently denied. Enable it from settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        }
}
