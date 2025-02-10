package br.com.brilliantmachine.get_background_location

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*

class GeolocalizacaoService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val request = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    println("Nova localização: ${location.latitude}, ${location.longitude}")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

        startForeground(1, criarNotificacao())
    }

    private fun criarNotificacao(): Notification {
        val canalId = "geolocalizacao_channel"
        val manager = getSystemService(NotificationManager::class.java)

        val canal = NotificationChannel(
            canalId, "Serviço de Geolocalização",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(canal)

        return NotificationCompat.Builder(this, canalId)
            .setContentTitle("Serviço de Localização")
            .setContentText("Capturando sua localização em background.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
