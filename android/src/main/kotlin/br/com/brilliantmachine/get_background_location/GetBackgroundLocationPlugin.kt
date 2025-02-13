package br.com.brilliantmachine.get_background_location

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

/** MeuPlugin */
class GetBackgroundLocationPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel: MethodChannel
  private var activity: Activity? = null
  private lateinit var context: Context
  private lateinit var eventChannel: EventChannel

  override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    context = binding.applicationContext
    channel = MethodChannel(binding.binaryMessenger, "get_background_location")
    channel.setMethodCallHandler(this)

    eventChannel = EventChannel(binding.binaryMessenger, "location_stream")
    eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        GeolocationService.eventSink = events
      }

      override fun onCancel(arguments: Any?) {
        GeolocationService.eventSink = null
      }
    })
  }
  fun getAppName(): String {
    val packageManager: PackageManager = context.packageManager
    val applicationInfo = context.applicationInfo
    val appName = packageManager.getApplicationLabel(applicationInfo) as String
    Log.e("erro nome","name $appName")
    return appName
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "startService" -> {
        val interval = call.argument<Int>("CHECK_INTERVAL")?.toLong() ?: 10000
        val intent = Intent(context, GeolocationService::class.java).apply {
          putExtra("CHECK_INTERVAL",interval)
          putExtra("APP_NAME",getAppName())
        }
        context.startForegroundService(intent)
        result.success("Serviço de localização iniciado")
      }
      "stopService" -> {
        val intent = Intent(context, GeolocationService::class.java)
        context.stopService(intent)
        result.success("Serviço de localização parado")
      }
      else -> result.notImplemented()
    }
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

 override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
   channel.setMethodCallHandler(null)
 }

}