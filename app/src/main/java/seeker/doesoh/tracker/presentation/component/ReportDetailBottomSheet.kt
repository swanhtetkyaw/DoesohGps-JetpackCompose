package seeker.doesoh.tracker.presentation.component


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import seeker.doesoh.tracker.R


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportDetailBottomSheet() {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
   ModalBottomSheetLayout(
       sheetContent = {
       ReportDetailBody()
   },
       sheetBackgroundColor = colorResource(id = R.color.background_white),
       sheetShape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp),
       sheetState = bottomSheetState) {

   }
}

@Composable
fun ReportDetailBody() {
    BoxWithConstraints(modifier = Modifier
        .fillMaxWidth()
        .heightIn(300.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "Device Name", fontSize = 18.sp, fontWeight = FontWeight.Light)
        }
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")

    }
}

@Preview(showBackground = true)
@Composable
fun ReportDetailPreview() {
    ReportDetailBottomSheet()
}