package com.example.saasfinanzas.features.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saasfinanzas.ui.theme.greenPrimary
import com.example.saasfinanzas.ui.theme.white




@Composable
fun PrimaryButton(text: String,onClick: () -> Unit){
   Button(
       onClick = onClick,
       shape = RoundedCornerShape(5.dp),
       colors = ButtonDefaults.buttonColors(
           containerColor = greenPrimary,
           contentColor = white
       ),
       modifier = Modifier
           .fillMaxWidth()
           .padding(20.dp)
           .height(50.dp)){
       Text(text)

   }

}

@Composable
fun Alert(text: String, title:String, showDialog: Boolean){
    AlertDialog(
        onDismissRequest = { showDialog },
        title = { title},
        text = { text },
        confirmButton = {
            TextButton(onClick = {
                showDialog
            }) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = { showDialog }) { Text("Cancelar") }
        }
    )

}


//@Preview(showBackground = true)
//@Composable
//private fun Button() {
//    Button(onClick = {},
//        shape = RoundedCornerShape(5.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = greenPrimary,
//            contentColor = white
//        ),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(20.dp)
//            .height(50.dp)){
//        Text("boton" )
//
//    }
//
//}