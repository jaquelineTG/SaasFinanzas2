package com.example.saasfinanzas.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.saasfinanzas.MainActivity // Asegúrate de que apunte a tu Activity principal
import com.example.saasfinanzas.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Se dispara cuando el dispositivo recibe su "Token" único por primera vez
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "Mi token único es: $token")
        // 🔹 Aquí deberías enviar este token a Firestore para guardarlo en el perfil del usuario
        // Ej: viewModelAuth.saveTokenToFirestore(token)
    }

    // Se dispara cuando recibes una notificación de Firebase
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM_MESSAGE", "Mensaje recibido de: ${message.from}")

        // Verificamos si el mensaje trae una notificación visible
        message.notification?.let {
            val titulo = it.title ?: "SaaSFinanzas"
            val cuerpo = it.body ?: "Tienes un nuevo mensaje"

            mostrarNotificacion(titulo, cuerpo)
        }
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        // Intent para que al tocar la notificación se abra la app
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "saasfinanzas_alertas_premium"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Construimos el diseño de la notificación
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.welcome1) // 🔹 Cambia esto por el ícono de tu app si tienes uno transparente
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true) // Se borra al tocarla
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Para que aparezca como "tarjeta" flotante (Heads-up)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificaciones (Obligatorio en Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas Premium",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificaciones de presupuestos y metas"
            notificationManager.createNotificationChannel(channel)
        }

        // Mostramos la notificación (El ID 0 puede cambiarse si quieres mostrar varias notificaciones a la vez sin que se sobreescriban)
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}