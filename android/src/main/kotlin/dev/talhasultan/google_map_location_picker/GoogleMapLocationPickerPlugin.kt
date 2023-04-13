package dev.talhasultan.google_map_location_picker

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import android.content.pm.PackageManager
import java.math.BigInteger
import java.security.MessageDigest
import android.content.pm.PackageInfo

class GoogleMapLocationPickerPlugin : FlutterPlugin, MethodCallHandler, ActivityAware  {
    private lateinit var channel : MethodChannel
    private var activityBinding: ActivityPluginBinding? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "google_map_location_picker")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if(activityBinding == null) {
        result.notImplemented()
        return
    }
    if (call.method == "getSigningCertSha1") {
        try {
            //.orEmpty() added on 14/05/22 by mg to call.arguments<String>().orEmpty()
            val info: PackageInfo = activityBinding!!.activity.packageManager.getPackageInfo(call.arguments<String>().orEmpty(), PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA1")
                md.update(signature.toByteArray())

                val bytes: ByteArray = md.digest()
                val bigInteger = BigInteger(1, bytes)
                val hex: String = String.format("%0" + (bytes.size shl 1) + "x", bigInteger)

                result.success(hex)
            }
        } catch (e: Exception) {
            result.error("ERROR", e.toString(), null)
        }
    } else if (call.method == "getCurrentLocation") {
        if (ContextCompat.checkSelfPermission(activityBinding!!.activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(result)
        } else {
            ActivityCompat.requestPermissions(activityBinding!!.activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    } else if (call.method == "onRequestPermissionsResult") {
        if (call.argument<Int>("requestCode") == LOCATION_PERMISSION_REQUEST_CODE) {
            if (call.argument<Int>("grantResults")?.get(0) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(result)
            } else {
                result.error("ERROR", "Location permission denied", null)
            }
        }
    } else {
        result.notImplemented()
    }
}

private fun getCurrentLocation(result: Result) {
    try {
        val locationManager = activityBinding!!.activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            result.error("ERROR", "Location services disabled", null)
        } else {
            var location: Location? = null

            if (isGpsEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }

            if (location == null && isNetworkEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }

            if (location != null) {
                result.success(getLocationJson(location))
            } else {
                result.error("ERROR", "Unable to get current location", null)
            }
        }
    } catch (e: Exception) {
        result.error("ERROR", e.toString(), null)
    }
}

private fun getLocationJson(location: Location): String {
    val jsonObject = JSONObject()

    jsonObject.put("latitude", location.latitude)
    jsonObject.put("longitude", location.longitude)

    return jsonObject.toString()
}


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
    }

    override fun onDetachedFromActivity() {
        activityBinding = null
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }
}