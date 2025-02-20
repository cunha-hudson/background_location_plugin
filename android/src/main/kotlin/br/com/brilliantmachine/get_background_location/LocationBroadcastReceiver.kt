package br.com.brilliantmachine.get_background_location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LocationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "ACTION_LOCATION_UPDATE") {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)

            val locationData = mapOf("latitude" to latitude, "longitude" to longitude)

            // Envia os dados via Broadcast para o Flutter
            val eventChannelIntent = Intent("com.example.yourapp.LOCATION_UPDATE")
            eventChannelIntent.putExtra("latitude", latitude)
            eventChannelIntent.putExtra("longitude", longitude)

            context?.sendBroadcast(eventChannelIntent)
        }
    }
}