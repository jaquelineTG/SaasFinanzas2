package com.gastario.app.data.repository

import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val firestore = FirebaseFirestore.getInstance()

    // Inicializamos el cliente de pagos de Google

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts() // Le decimos a Google qué tipo de pagos pendientes aceptar
                .build()
        )
        .build()

    fun iniciarConexion(uidUsuario: String) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BILLING", "¡Conectado a Google Play exitosamente!")
                    // Aquí luego buscaremos si el usuario ya tenía compras previas
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("BILLING", "Se desconectó de Google Play. Reintentando...")
                // Aquí se puede reintentar la conexión
            }
        })
    }

    // Esta función es llamada AUTOMÁTICAMENTE por Google cuando el usuario termina de pagar
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                manejarCompraExitosa(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("BILLING", "El usuario canceló la compra.")
        } else {
            Log.e("BILLING", "Error en la compra: ${billingResult.debugMessage}")
        }
    }

    private fun manejarCompraExitosa(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                // Le confirmamos a Google que ya entregamos el producto
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d("BILLING", "¡Compra confirmada ante Google!")
                        // TODO: Aquí actualizaremos el isPremium = true en Firestore
                    }
                }
            }
        }
    }

    // 1. Preguntamos a Google por el precio y detalles de la suscripción
    fun obtenerDetallesDelProducto(idSuscripcion: String, alObtener: (ProductDetails?) -> Unit) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(idSuscripcion)
                .setProductType(BillingClient.ProductType.SUBS) // Le decimos que es una suscripción
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { resultado, listaDeProductos ->
            // En esta versión, 'listaDeProductos' ya viene directa, no necesitamos extraerla

            if (resultado.responseCode == BillingClient.BillingResponseCode.OK && !listaDeProductos.isNullOrEmpty()) {
                Log.d("BILLING", "¡Suscripción encontrada en Google Play!")
                alObtener(listaDeProductos[0])
            } else {
                Log.e("BILLING", "Error o suscripción no encontrada: ${resultado.debugMessage}")
                alObtener(null)
            }
        }
    }

    // 2. Esta función lanza la ventana oficial de pagos de Google
    fun iniciarCompra(activity: android.app.Activity, productDetails: ProductDetails) {
        // En suscripciones de Google, cada producto tiene "ofertas" (ej. precio normal, mes gratis)
        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken

        if (offerToken == null) {
            Log.e("BILLING", "Error: No se encontró el token de la oferta.")
            return
        }

        val parametrosProducto = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val parametrosDeCompra = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(parametrosProducto)
            .build()

        // ¡Esta línea despliega la interfaz de cobro en el celular!
        billingClient.launchBillingFlow(activity, parametrosDeCompra)
    }
}