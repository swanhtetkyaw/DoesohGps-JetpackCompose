package seeker.doesoh.tracker.presentation.screen


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import seeker.doesoh.tracker.R
import seeker.doesoh.tracker.presentation.LogIn.LogInEvent
import seeker.doesoh.tracker.presentation.MainViewModel
import seeker.doesoh.tracker.presentation.Screen
import seeker.doesoh.tracker.util.Constant.BASE_URL
import seeker.doesoh.tracker.util.Constant.BASE_URL_MAP
import seeker.doesoh.tracker.util.Constant.BASE_URL_SHORT_LIST

@Composable
fun LogInScreen(
    mainViewModel: MainViewModel,
    navController: NavController) {
    val state = mainViewModel.logInState.value
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if(!state.isAuth) {
//            Button(onClick = {
//                mainViewModel.onEvent(LogInEvent.Authenticate("kmac","doesoh@admin112"))
//            }) {
//                Text(text = "LogIn")
//            }
            LoginBody(mainViewModel = mainViewModel)

            if(state.error.isNotBlank()) {
                Log.d("login", state.error)
            }
        }

        if(state.isAuth) {
            LaunchedEffect(key1 = Unit) {
                navController.popBackStack()
                navController.navigate(Screen.MapScreen.route)
            }
        }

        if(state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f), RectangleShape),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        }


    }
}

@Composable
fun LoginBody(mainViewModel: MainViewModel) {
    var username by rememberSaveable() {
        mutableStateOf(mainViewModel.usernamePreference ?: "")
    }
    var password by rememberSaveable() {
        mutableStateOf(mainViewModel.passwordPreference ?: "")
    }

    var selectedServer by rememberSaveable() {
       mutableStateOf(mainViewModel.shortUrlPreference ?: BASE_URL_SHORT_LIST[0])
    }

    Log.d("LogIn", "$username $password $selectedServer")
 Box(modifier = Modifier
     .fillMaxSize()
     .background(color = colorResource(id = R.color.background_white), shape = RectangleShape)) {
     Image(painter = painterResource(id = R.drawable.ic_login_background), contentDescription = "backgroundImage", contentScale = ContentScale.FillBounds, modifier = Modifier
         .fillMaxSize()
         .padding(0.dp))
     //Icon
//     Row(modifier = Modifier.fillMaxWidth(),Arrangement.End) {
//         Image(
//             painter = painterResource(id = R.drawable.doesoh_logo),
//             contentDescription = "Logo",
//             contentScale = ContentScale.Fit,
//             modifier = Modifier
//                 .size(80.dp)
//                 .offset(x = -(50).dp, y = 70.dp)
//         )
//     }

     Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
         //Title
         Text(text = "DoeSoh Tracker", fontSize = 24.sp, fontWeight = FontWeight.Light, modifier = Modifier.offset(y = (-120).dp))
         //username and password
         UsernameTextField(label = "Username", modifier = Modifier.offset(y = -(100).dp), text = username, onTextChange = {username = it})
         PasswordTextField(label = "Password", modifier = Modifier.offset(y = -(90).dp),text = password, onTextChange = {password = it})
         // dropdown
         Row(modifier = Modifier.offset(x = 75.dp, y = -(80).dp), horizontalArrangement = Arrangement.End) {
             dropDownMenuBox(selectedOptionText = selectedServer, onSelectedOptionTextChange = { selectedServer = it })
         }
         //Login Button
         Button(
             onClick = {
                 val selectedBaseUrl = BASE_URL_MAP.getOrDefault(selectedServer, BASE_URL)
                 Log.d("LogIn", "$selectedBaseUrl")
                 mainViewModel.onLoginEvent(LogInEvent.Authenticate(username,password,selectedBaseUrl,selectedServer))
             },
             colors = ButtonDefaults.buttonColors(colorResource(id = R.color.logInColor)),
             elevation = ButtonDefaults.elevation(10.dp),
             modifier = Modifier
                 .height(50.dp)
                 .width(120.dp)
                 .offset(y = -(20.dp))) {
             Text(text = "LogIn", color = Color.White, fontSize = 18.sp)
         }
     }
 }
}


@Composable
fun UsernameTextField(modifier: Modifier = Modifier,label: String,text: String,onTextChange: (String) -> Unit) {

    OutlinedTextField(
        value = text,
        onValueChange = {
        onTextChange(it)
    },
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp),
        label = {Text(text = label)},
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = colorResource(id = R.color.colorPrimary),
            unfocusedIndicatorColor = colorResource(id = R.color.colorPrimary),
            disabledIndicatorColor = colorResource(id = R.color.colorPrimary),
            cursorColor = Color.Black,
            backgroundColor = colorResource(id = R.color.background_white)
        ),
        modifier = modifier
            .height(60.dp)
            .width(280.dp))
}

@Composable
fun PasswordTextField(modifier: Modifier = Modifier,label: String,text: String,onTextChange: (String) -> Unit) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = text,
        onValueChange = {
            onTextChange(it)
        },
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp),
        label = {Text(text = label)},
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = colorResource(id = R.color.colorPrimary),
            unfocusedIndicatorColor = colorResource(id = R.color.colorPrimary),
            disabledIndicatorColor = colorResource(id = R.color.colorPrimary),
            cursorColor = Color.Black,
            backgroundColor = colorResource(id = R.color.background_white)
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        },
        modifier = modifier
            .height(60.dp)
            .width(280.dp))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun dropDownMenuBox(modifier: Modifier = Modifier,selectedOptionText: String,onSelectedOptionTextChange: (String) -> Unit) {
    val servers = BASE_URL_SHORT_LIST
    var expanded by remember {
        mutableStateOf(false)
    }
//    var selectedOptionText by remember { mutableStateOf(servers[0]) }
//    Row(modifier = Modifier
//        .fillMaxWidth()
//        .offset(x = -(50).dp, y = -(80).dp), horizontalArrangement = Arrangement.End) {

        ExposedDropdownMenuBox(
            expanded = expanded ,
            onExpandedChange = {
                expanded = !expanded
            }) {

                OutlinedTextField(
                    readOnly = true,
                    value = selectedOptionText,
                    shape = RoundedCornerShape(10.dp),
                    onValueChange = {},
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded )
                    },
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp),
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        backgroundColor = colorResource(id = R.color.colorSecondary),
                        trailingIconColor = Color.White
                    )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                servers.forEach { selectionOption  ->
                    DropdownMenuItem(
                        onClick = {
                            onSelectedOptionTextChange(selectionOption)
                            expanded = false
                        }
                    ) {
                        Text(text = selectionOption)
                    }
                }
            }
        }

//    }
}

@Composable
fun LogInBodyTwo() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.loginbackground),
            contentDescription = "LoginBackground" ,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize())

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.background_white),
                        Color.Transparent,
                    ),
                    startY = 900f
                )
            ))
        
        Column(
            Modifier
                .fillMaxSize()
                .offset(y = -(90.dp)), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.doesoh_logo),
                contentDescription = "Doesoh Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(10.dp)
                    .offset(y = -(20.dp)))

//            AuthTextField(label = "UserName", modifier = Modifier.padding(10.dp))
//            AuthTextField(label = "Password", modifier = Modifier.padding(bottom = 10.dp))
            Row(modifier = Modifier.offset(x = 75.dp)) {
//                dropDownMenuBox()
            }
            
            Button(
                onClick = { /*TODO*/ },
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 10.dp,
                    disabledElevation = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.colorPrimary)
                ),
                modifier = Modifier
                    .width(140.dp)
                    .height(45.dp)
                    .offset(y = 50.dp)) {
                Text(text = "LogIn",color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LogInPreview() {
//    LoginBody()
//    LogInBodyTwo()
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        AuthTextField(text = "Username")
//    }
}