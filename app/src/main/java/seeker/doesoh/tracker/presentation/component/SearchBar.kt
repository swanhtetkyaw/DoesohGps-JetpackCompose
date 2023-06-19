package seeker.doesoh.tracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.presentation.screen.MyFloatingActionButton


@Composable
fun SearchBar(state: MutableState<TextFieldValue>,modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = state.value,
        placeholder = { Text(text = "Search Car By Name...", fontSize = 16.sp, color = Color.Gray)},
        onValueChange = { text ->
            state.value = text
        },
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clearFocusOnKeyboardDismiss(),
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp),
                tint = Color.Black )

        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            backgroundColor = colorResource(id = R.color.background_white)
        ),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
        }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )
}

@OptIn(ExperimentalLayoutApi::class)
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    val testState = remember {
        mutableStateOf(TextFieldValue(""))
    }

    Row(modifier = Modifier
        .offset(10.dp, 20.dp)
        .fillMaxSize()
        .wrapContentHeight(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        MyFloatingActionButton(
            onClick = {

            },
            iconImage = Icons.Outlined.Menu,
            modifier = Modifier.size(40.dp),
            iconModifier = Modifier.size(25.dp))

        SearchBar(state = testState)
    }
}