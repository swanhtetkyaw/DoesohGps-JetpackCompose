package seeker.doesoh.tracker.presentation.component

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview



@Composable
fun MyAlertDialog(openDialog: Boolean,typeString: String,onCloseDialog: () -> Unit,action: () -> Unit) {
   if(openDialog) {
       AlertDialog(
           onDismissRequest = { onCloseDialog() },
           title = {
               Text(text = typeString)
           },
           text = {
               Text(text = "Are you sure you want to $typeString ?")
           },
           confirmButton = {
               Button(onClick = {
                   action()
                   onCloseDialog()
               }) {
                   Text(text = "Confirm")
               }
           },
           dismissButton = {
               Button(onClick = {
                   onCloseDialog()
               }) {
                   Text(text = "Cancel")
               }
           }
            )
   }
}


@Preview(showBackground = true)
@Composable
private fun PreviewAlertDialog() {
    MyAlertDialog(openDialog = true, onCloseDialog = {}, typeString = "Engine Cut" ){}
}
