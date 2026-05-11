import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current

    // Solo pedimos permiso si el celular tiene Android 13 o superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        // Este es el "lanzador" que mostrará el cuadro de diálogo del sistema
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // El usuario aceptó, podemos mandarle notificaciones
                println("Permiso de notificaciones CONCEDIDO")
            } else {
                // El usuario rechazó
                println("Permiso de notificaciones DENEGADO")
            }
        }

        // Revisamos si ya nos habían dado el permiso antes
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        // Si no tenemos el permiso, lo pedimos automáticamente al entrar a la pantalla
        LaunchedEffect(Unit) {
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}