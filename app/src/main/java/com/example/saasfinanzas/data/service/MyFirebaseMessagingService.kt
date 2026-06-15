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

        // 👇 1. YA NO CARGAMOS EL LOGO A COLOR (Eliminamos BitmapFactory)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            // 👇 2. Ponemos tu silueta transparente a la izquierda
            .setSmallIcon(R.drawable.notificacionicon)

            // 👇 3. EL TRUCO MAGICO: Le decimos a Android que pinte el círculo de fondo
            // Cambia el "#FBB03B" por el código HEX del color amarillo/verde de tu marca
            .setColor(android.graphics.Color.parseColor("#FBB03B"))

            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // 👇 4. BORRAMOS la línea .setLargeIcon() para dejar la derecha vacía

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas Premium",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notificaciones de presupuestos y metas"
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}