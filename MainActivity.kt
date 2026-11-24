package com.kani.offlinemap

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var locationManager: LocationManager
    private var tracking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.webViewClient = WebViewClient()
        web.webChromeClient = WebChromeClient()
        web.addJavascriptInterface(AndroidBridge(), "AndroidGPS")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    private fun hasLocationPermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            100
        )
    }

    private fun emitLocation(location: Location) {
        val js = "window.onNativeLocation && window.onNativeLocation(${location.latitude},${location.longitude},${location.accuracy});"
        runOnUiThread { web.evaluateJavascript(js, null) }
    }

    private val listener = object : LocationListener {
        override fun onLocationChanged(location: Location) { emitLocation(location) }
    }

    private fun beginTracking() {
        if (!hasLocationPermission()) { requestLocationPermission(); return }
        try {
            tracking = true
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, listener)
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10f, listener)
            }
        } catch (_: SecurityException) { }
    }

    private fun endTracking() {
        tracking = false
        try { locationManager.removeUpdates(listener) } catch (_: SecurityException) { }
    }

    inner class AndroidBridge {
        @JavascriptInterface fun getLocation() {
            if (!hasLocationPermission()) { requestLocationPermission(); return }
            try {
                val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (last != null) emitLocation(last) else beginTracking()
            } catch (_: SecurityException) { }
        }
        @JavascriptInterface fun startTracking() { runOnUiThread { beginTracking() } }
        @JavascriptInterface fun stopTracking() { runOnUiThread { endTracking() } }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
            if (tracking) beginTracking() else getLastLocationAfterPermission()
        }
    }

    private fun getLastLocationAfterPermission() {
        try {
            val last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (last != null) emitLocation(last) else beginTracking()
        } catch (_: SecurityException) { }
    }

    override fun onDestroy() {
        endTracking()
        web.destroy()
        super.onDestroy()
    }
}
