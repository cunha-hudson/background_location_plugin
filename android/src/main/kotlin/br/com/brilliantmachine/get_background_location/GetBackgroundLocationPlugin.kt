package br.com.brilliantmachine.get_background_location

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

/** MeuPlugin */
class GetBackgroundLocationPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

  private lateinit var channel: MethodChannel
  private var activity: Activity? = null
  private lateinit var context: Context
  private lateinit var eventChannel: EventChannel

  private val locationReceiver = LocationBroadcastReceiver()

  override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    context = binding.applicationContext
    channel = MethodChannel(binding.binaryMessenger, "get_background_location")
    channel.setMethodCallHandler(this)

    eventChannel = EventChannel(binding.binaryMessenger, "location_stream")
    eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        // Registrando o BroadcastReceiver
        val intentFilter = IntentFilter("ACTION_LOCATION_UPDATE")
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              context.registerReceiver(locationReceiver, intentFilter, Context.RECEIVER_EXPORTED)
          }else{
            context.registerReceiver(locationReceiver, intentFilter)
          }
      }

      override fun onCancel(arguments: Any?) {
        Log.d("GetBackgroundLocationPlugin", "Flutter parou de ouvir eventos de localização.")
        context.unregisterReceiver(locationReceiver) // Desregistrando o receiver
      }
    })
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "startService" -> {
        if (!isLocationEnabled()) {
          showNotification("Geolocalização desativada", "Por favor, ative a geolocalização nas configurações.")
          result.error("LOCATION_DISABLED", "Os serviços de localização estão desativados.", null)
          return
        }

        if (!checkPermissions()) {
          showNotification("Permissões de localização não concedidas", "Por favor, conceda as permissões necessárias nas configurações do dispositivo.")
          result.error("PERMISSION_DENIED", "Permissões de localização não concedidas.", null)
          return
        }

        val interval = call.argument<Int>("CHECK_INTERVAL")?.toLong() ?: 10000
        val intent = Intent(context, GeolocationService::class.java).apply {
          putExtra("CHECK_INTERVAL", interval)
          putExtra("APP_NAME", getAppName())
        }
        Log.d("location:","Serviço de localização iniciado")
        context.startService(intent)
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

  private fun isLocationEnabled(): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
  }

  private fun checkPermissions(): Boolean {
    return activity?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            activity?.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
  }

  private fun getAppName(): String {
    val packageManager: PackageManager = context.packageManager
    val applicationInfo = context.applicationInfo
    return packageManager.getApplicationLabel(applicationInfo) as String
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

  private fun showNotification(title: String, message: String) {
    val channelId = "geolocalizacao_channel"
    val manager = context.getSystemService(NotificationManager::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(channelId, "Serviço de Localização", NotificationManager.IMPORTANCE_HIGH)
      manager?.createNotificationChannel(channel)
    }

    val intent = context.packageManager?.getLaunchIntentForPackage(context.packageName)
    val pendingIntent = PendingIntent.getActivity(
      context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
      .setContentTitle(title)
      .setContentText(message)
      .setSmallIcon(android.R.drawable.ic_menu_mylocation)
      .setContentIntent(pendingIntent)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build()

    manager?.notify(1, notification)
  }
}
