package com.example.saasfinanzas.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Esta función se dispara cuando el dispositivo recibe su "Token" único por primera vez
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Mi token único es: $token")
        // En la siguiente fase, mandaremos este token a Firestore
    }

    // Esta función se dispara cuando recibes una notificación y la app está ABIERTA (Foreground)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM_MESSAGE", "Mensaje recibido de: ${message.from}")

        // Si el mensaje trae una notificación visible
        message.notification?.let {
            Log.d("FCM_MESSAGE", "Título: ${it.title} | Cuerpo: ${it.body}")
            // Aquí luego escribiremos el código para mostrar la "tarjetita" de notificación
            // en la parte superior de la pantalla
        }
    }
}