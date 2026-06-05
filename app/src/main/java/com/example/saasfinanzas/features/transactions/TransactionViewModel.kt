package com.example.saasfinanzas.features.transactions

import androidx.lifecycle.ViewModel
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.repository.AuthRepository
import com.example.saasfinanzas.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos

    fun cargarMovimientos() {
        val uid = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            val result = repository.getMovimientos(uid)
            result.onSuccess {
                _movimientos.value = it
            }
        }
    }

    // Agregamos un "callback" (onSuccess) que se ejecutará solo cuando termine
    fun addMovimiento(movimiento: Movimiento, onSuccess: () -> Unit) {

        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.addMovimiento(uid, movimiento)

            result.onSuccess {
                println("Guardado correctamente")
                // Le avisamos a la pantalla que ya es seguro cerrarse
                onSuccess()
            }.onFailure {
                println("Error: ${it.message}")
            }
        }
    }

    fun exportarMovimientosACSV(context: Context, movimientosAExportar: List<Movimiento>): Uri? {
        if (movimientosAExportar.isEmpty()) return null

        try {
            // 1. Crear el nombre del archivo con la fecha actual
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Reporte_Gastos_$timeStamp.csv"

            // 2. Crear el archivo temporal en el caché de la app
            val file = File(context.cacheDir, fileName)
            val writer = FileWriter(file)

            // 3. Escribir las cabeceras (Columnas del Excel)
            writer.append("Fecha,Descripción,Categoría ID,Monto,Tipo\n")

            // 4. Escribir cada movimiento
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            movimientosAExportar.forEach { mov ->
                val fechaString = dateFormat.format(Date(mov.fecha))

                // Limpiamos la descripción por si el usuario puso comas (eso rompería el CSV)
                val descLimpia = mov.descripcion.replace(",", " ")

                writer.append("$fechaString,$descLimpia,${mov.categoriaId},${mov.monto},${mov.tipo}\n")
            }

            writer.flush()
            writer.close()

            // 5. Devolver la URI usando FileProvider para poder compartirlo con otras apps
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun deleteMovimiento(movimientoId: String) {

        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {

            repository.deleteMovimiento(
                uid,
                movimientoId
            ).onSuccess {

                _movimientos.value =
                    _movimientos.value.filter {
                        it.id != movimientoId
                    }

            }
        }
    }

    fun updateMovimiento(
        movimiento: Movimiento
    ) {

        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {

            repository.updateMovimiento(
                uid,
                movimiento
            ).onSuccess {

                _movimientos.value =
                    _movimientos.value.map {

                        if (it.id == movimiento.id)
                            movimiento
                        else
                            it
                    }
            }
        }
    }
}