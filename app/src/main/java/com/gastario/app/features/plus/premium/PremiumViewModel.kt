package com.gastario.app.features.plus.premium

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), PurchasesUpdatedListener {

    // Precios visuales (Se pueden actualizar dinámicamente desde Google Play si lo deseas en el futuro)
    var precioMensual by mutableStateOf("$49.00 MXN")
        private set
    var precioAnual by mutableStateOf("$399.00 MXN")
        private set

    // Estado reactivo para avisarle a la UI que muestre el modal de éxito
    private val _compraExitosa = MutableStateFlow(false)
    val compraExitosa: StateFlow<Boolean> = _compraExitosa

    private var billingClient: BillingClient
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        // Inicializamos el cliente oficial de pagos de Google Play
        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        conectarAGooglePlay()
    }

    private fun conectarAGooglePlay() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BILLING", "Conectado a Google Play Billing exitosamente.")
                }
            }
            override fun onBillingServiceDisconnected() {
                // Reintentar conexión si se cae
                Log.w("BILLING", "Desconectado de Google Play. Reintentando...")
                conectarAGooglePlay()
            }
        })
    }

    //  Lanza la ventana de cobro nativa de Google Play
    fun iniciarFlujoCompra(activity: Activity, productId: String) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]

                // En suscripciones, extraemos el token de la oferta base
                val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken)
                                .build()
                        )
                    )
                    .build()

                billingClient.launchBillingFlow(activity, billingFlowParams)
            } else {
                Log.e("BILLING", "Error al buscar el producto. Asegúrate de que el ID exista en Google Play Console.")
            }
        }
    }

    // 💳 Respuesta automática de Google Play al procesar la tarjeta (Aprobado, Declinado, Cancelado)
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                manejarCompraExitosa(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.w("BILLING", "El usuario canceló la ventana de pago.")
        } else {
            Log.e("BILLING", "Error en la compra: Código ${billingResult.responseCode}")
        }
    }

    private fun manejarCompraExitosa(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                // Confirmamos la compra a Google para que no haga reembolso automático a los 3 días
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        actualizarUsuarioPremiumEnFirestore()
                    }
                }
            } else {
                actualizarUsuarioPremiumEnFirestore()
            }
        }
    }

    private fun actualizarUsuarioPremiumEnFirestore() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("usuarios").document(uid)
            .update("isPremium", true)
            .addOnSuccessListener {
                Log.d("BILLING", "¡Firestore actualizado con éxito! Usuario ahora es Premium.")
                // Disparamos la alerta de éxito hacia la UI para mostrar el Modal
                _compraExitosa.value = true
            }
            .addOnFailureListener {
                Log.e("BILLING", "Error al guardar el estatus en Firestore: ${it.message}")
            }
    }

    fun resetCompraExitosa() {
        _compraExitosa.value = false
    }
}