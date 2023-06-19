package seeker.doesoh.tracker.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import seeker.doesoh.tracker.ui.theme.DoeSohTrackerTheme
import java.util.*


const val TAG = "Main"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.user.observe(this) { user ->
//            Log.d("user", user.name)
////            viewModel.synFcmToken(user)
//        }
//
//        viewModel.devices.observe(this) { devices ->
//            devices.forEach { (id,device) ->
//                Log.d("UpdateDevices", "Devices: $id => ${device.name} ${device.status} ${device.category}")
//            }
//
//        }
//
//        viewModel.update.observe(this) { update ->
//            Log.d(TAG, "Update received!! " )
//        }

        lifecycleScope.launch {
            viewModel.toastEvent.collect { text ->
                Log.d(TAG, "Toast: $text")
                Toast.makeText(this@MainActivity,text,Toast.LENGTH_LONG).show()
            }
        }

        // Yes I Don't Care :D
        viewModel.logoutFlow.observe(this) { isLogout ->
            if(isLogout) {
                finish()
                startActivity(intent)
            }
        }
        setContent {
            DoeSohTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Navigation(mainViewModel = viewModel)
                }
            }
        }
    }

}

