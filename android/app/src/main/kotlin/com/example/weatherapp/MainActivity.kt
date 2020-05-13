package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity : FlutterActivity() {

    private lateinit var locationManager: LocationManager
    private val CHANNEL = "com.wayfair.flutter/android"
    private val controller = CurrentWeatherResponseController()

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        startRequestPermissionFlow()
        registerFlutterMethods(flutterEngine)
    }

    private fun registerFlutterMethods(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method.contentEquals("getLocation")) {
                val location = requestLocation()
                val geocoder = Geocoder(this)
                val addressResult = geocoder.getFromLocation(location.latitude, location.longitude, 1).firstOrNull()
                addressResult?.let {
                    result.success(it.locality)
                }
            } else if (call.method == "fetchCurrentWeather") {
                controller.getWeather(call.argument("location")!!, result)
            }
        }
    }

    private fun startRequestPermissionFlow() {
        val isNetworkEnabled = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!isNetworkEnabled) {
            requestLocationPermissions()
        } else {
            requestLocation()
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }

    private fun requestLocation(): Location {
        val location = locationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        Log.d(TAG, "onLocationChanged ${location.latitude} ${location.longitude}")
        return location
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation()
        }
    }

    companion object {
        const val TAG = "WeatherApp"
    }
}
