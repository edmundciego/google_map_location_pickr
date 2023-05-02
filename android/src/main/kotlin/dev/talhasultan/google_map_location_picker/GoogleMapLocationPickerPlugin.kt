package dev.talhasultan.google_map_location_picker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest

class GoogleMapLocationPickerPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private var activityBinding: ActivityPluginBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
                val info = activityBinding!!.activity.packageManager.getPackageInfo(call.arguments<String>().orEmpty(), PackageManager.GET_SIGNATURES)
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activityBinding!!.activity)
        fusedLocationClient.lastLocation
            .addOnSuccessListener(OnSuccessListener<Location> { location ->
                if (location != null) {
                    result.success(getLocationJson(location))
                } else {
                    result.error("ERROR", "Unable to get current location", null)
                }
            })
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
