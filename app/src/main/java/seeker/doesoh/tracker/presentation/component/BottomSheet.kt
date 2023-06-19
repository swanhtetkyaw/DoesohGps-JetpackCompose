package seeker.doesoh.tracker.presentation.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.data.remote.Device


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(state: ModalBottomSheetState,devices: Map<Long,Device>,onDeviceClicked: (id: Long) -> Unit) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = !state.isVisible) {
        scope.launch {
            focusManager.clearFocus()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            BottomSheetBody(devices,onDeviceClicked = onDeviceClicked)
        },
        sheetBackgroundColor = colorResource(id = R.color.colorAccent),
        sheetContentColor = Color.White,
        sheetShape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp),
        sheetState = state
    ) {

    }
}


@Composable
fun BottomSheetBody(devices: Map<Long,Device>,onDeviceClicked: (id: Long) -> Unit) {
    val textState = remember {
        mutableStateOf(TextFieldValue(""))
    }
    val deviceList = devices.values.toList().filter {
            val deviceName = it.name.replace("\\s".toRegex(),"")
            val searchWord = textState.value.text.replace("\\s".toRegex(),"")
            deviceName.contains(searchWord,ignoreCase = true)
    }

    Log.d("BottomSheetBody:", "${deviceList.size}")
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .height(500.dp)) {
        SearchBar(state = textState)
        Spacer(modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.background_white)))
        BottomSheetItemList(devices = deviceList, onDeviceClicked = onDeviceClicked)
    }
}


@Composable
fun BottomSheetItemList(
    devices: List<Device>,
    onDeviceClicked: (id: Long) -> Unit) {
    LazyColumn() {
        items(devices) { device ->
            BottomSheetItem(device,onDeviceClicked)
//            Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.White))
        }
    }
}

//fun Modifier.verticalScrollDisabled() =
//    pointerInput(Unit) {
//        awaitPointerEventScope {
//            while (true) {
//                awaitPointerEvent(pass = PointerEventPass.Initial).changes.forEach {
//                    val offset = it.positionChange()
//                    if (abs(offset.y) > 0f) {
//                        it.consume()
//                    }
//                }
//            }
//        }
//    }

