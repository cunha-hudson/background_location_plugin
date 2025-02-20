package br.com.brilliantmachine.get_background_location

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import io.flutter.plugin.common.EventChannel

class GeolocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var checkInterval: Long = 10000 // 10 segundos
    private var appName: String = "Location"

    private val handler = Handler(Looper.getMainLooper())
    private val permissionChecker = object : Runnable {
        override fun run() {
            if (!checkPermissions()) {
                showNotification("Permissão de localização não concedida.", "Por favor, conceda a permissão para o app funcionar corretamente.")
                stopSelf()
            } else {
                handler.postDelayed(this, checkInterval)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkInterval = intent?.getLongExtra("CHECK_INTERVAL", 10000) ?: 10000
        appName = intent?.getStringExtra("APP_NAME") ?: "Location"

        startForeground(1, criarNotificacao())

        if (!checkPermissions()) {
            showNotification("Permissão de localização não concedida.", "Por favor, conceda a permissão para o app funcionar corretamente.")
            stopSelf()
            return START_NOT_STICKY
        }
        if (!isLocationEnabled()) {
            stopSelf()
            showNotification("Geolocalização desativada", "Por favor, ative a geolocalização nas configurações do dispositivo.")
            return START_NOT_STICKY
        }

        update()
        handler.post(permissionChecker)

        return START_REDELIVER_INTENT
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            if (checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false
        }

        return true
    }

    private fun update() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setIntervalMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val locationData = mapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude
                    )
                    Log.d("location:","$locationData")

                    // Envia as atualizações via Broadcast
                    val intent = Intent("ACTION_LOCATION_UPDATE").apply {
                        putExtra("latitude", location.latitude)
                        putExtra("longitude", location.longitude)
                    }

                    sendBroadcast(intent)  // Envia o broadcast para todos os ouvintes
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun criarNotificacao(): Notification {
        val channelId = "geolocalizacao_channel"
        val channelName = "Serviço de Localização"
        val manager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        // Intent para abrir o app quando o usuário clicar na notificação
        val intent = packageManager?.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(appName)
            .setContentText("Toque para abrir o aplicativo.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent) // Associa o intent à notificação
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "geolocalizacao_channel"
        val channelName = "Serviço de Localização"
        val manager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }

        val intent = packageManager?.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager?.notify(1, notification)
    }
}
