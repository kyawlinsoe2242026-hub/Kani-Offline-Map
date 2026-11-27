package com.kani.offlinemap

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.JavascriptInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.content.Context
import android.webkit.WebSettings
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.addJavascriptInterface(
            GPSBridge(),
            "AndroidGPS"
        )

        setContentView(webView)

        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class GPSBridge {

        @JavascriptInterface
        fun getLocation() {
            runOnUiThread {
                requestGPS()
            }
        }

        @JavascriptInterface
        fun startTracking() {
            runOnUiThread {
                requestGPS()
            }
        }

        @JavascriptInterface
        fun stopTracking() {
            runOnUiThread {
                locationManager.removeUpdates(listener)
            }
        }
    }

    private val listener = LocationListener { location ->
        sendLocation(location)
    }

    private fun requestGPS() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            3000L,
            1f,
            listener
        )
    }

    private fun sendLocation(location: Location) {

        val lat = location.latitude
        val lon = location.longitude
        val accuracy = location.accuracy

        runOnUiThread {
            webView.evaluateJavascript(
                "window.onNativeLocation($lat,$lon,$accuracy)",
                null
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            requestGPS()
        }
    }

    override fun onDestroy() {
        locationManager.removeUpdates(listener)
        webView.destroy()
        super.onDestroy()
    }
}
