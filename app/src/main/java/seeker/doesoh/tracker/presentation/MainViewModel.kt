package seeker.doesoh.tracker.presentation

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.maps.android.compose.MarkerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.ByteString
import retrofit2.Retrofit
import seeker.doesoh.tracker.data.DoeSohApi
import seeker.doesoh.tracker.data.model.DateTimeISO
import seeker.doesoh.tracker.data.model.Fuel
import seeker.doesoh.tracker.data.model.Temperature
import seeker.doesoh.tracker.data.model.TemperatureDataset
import seeker.doesoh.tracker.data.remote.*
import seeker.doesoh.tracker.data.use_case.LogInUseCase
import seeker.doesoh.tracker.notification.GpsEventNotification
import seeker.doesoh.tracker.presentation.Chart.ChartData
import seeker.doesoh.tracker.presentation.LogIn.LogInEvent
import seeker.doesoh.tracker.presentation.LogIn.LogInState
import seeker.doesoh.tracker.presentation.ReportDetail.*
import seeker.doesoh.tracker.util.*
import seeker.doesoh.tracker.util.Constant.BASE_URL_SHORT_LIST
import seeker.doesoh.tracker.util.Constant.PASSWORD_PREFERENCE
import seeker.doesoh.tracker.util.Constant.SHORT_URL_PREFERENCE
import seeker.doesoh.tracker.util.Constant.USERNAME_PREFERENCE
import java.net.CookieManager
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val client: OkHttpClient,
    private val retrofit: Retrofit,
    private val LogInUseCase: LogInUseCase,
    private val sharedPreferences: SharedPreferences
): ViewModel(){
    //Log in state
    var currentUrl = ""
    val _logInState = mutableStateOf(LogInState())
    val logInState: State<LogInState> = _logInState
    val usernamePreference = sharedPreferences.getString(USERNAME_PREFERENCE,"")
    val passwordPreference = sharedPreferences.getString(PASSWORD_PREFERENCE,"")
    val shortUrlPreference = sharedPreferences.getString(SHORT_URL_PREFERENCE, BASE_URL_SHORT_LIST[0])
    //Report Mode
    var reportMode = mutableStateOf<Long?>(null)
    var showingRoute = mutableStateOf(false)
    private val _reportDetailUIState = mutableStateOf(ReportDetailUIState())
    val reportDetailUIState: State<ReportDetailUIState> = _reportDetailUIState
    var stopMarkerVisiable = mutableStateOf(true)
    var selectedTrip = mutableStateOf("")
    //Marker
    private val deviceLists = mutableMapOf<Long,Device>()
    val latestDevicePositionList = mutableMapOf<Long,Position>()
    val latestDeviceStatus = mutableMapOf<Long,String>()
    val markerStateList = mutableMapOf<Long,MarkerState>()
    var markerVisibility = mutableStateOf(true)
    //Notification
    private var _notificationEnabled = MutableStateFlow(sharedPreferences.getBoolean(GpsEventNotification.NOTIFICATION_ENABLE,false))
    val notificationEnabled = _notificationEnabled
    //Splash Screen
    var showSplashScreen = mutableStateOf(true)
    //logOut
    private val _logoutFlow = MutableLiveData<Boolean>()
    val logoutFlow = _logoutFlow
    //TODO:: change camera position with user data
    val cameraPosition = CameraPosition(LatLng(16.8310404,96.1280811),14f,0f,0f)

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user:LiveData<User> = _user

    private val _devices: MutableLiveData<Map<Long,Device>> = MutableLiveData()
    val devices: LiveData<Map<Long,Device>> = _devices

    private val _update: MutableLiveData<Update> = MutableLiveData()
    val update: LiveData<Update> = _update

    private val _events: MutableLiveData<Map<String,Event>> = MutableLiveData()
    val events: LiveData<Map<String,Event>> = _events

    private val _latestPositions: MutableLiveData<Map<Long,Position>> = MutableLiveData()
    val latestPositions: LiveData<Map<Long,Position>> = _latestPositions

    private val _tripReport: MutableStateFlow<List<TripReport>> = MutableStateFlow(emptyList())
    val tripReport = _tripReport

    var routeReportPolylineList: List<Position> = emptyList()
    private val _routeReportPolyLine: MutableStateFlow<List<LatLng>> = MutableStateFlow(emptyList())
    val routeReportPolyLine = _routeReportPolyLine

    private val _routeStopPoints: MutableStateFlow<List<StopReport>> = MutableStateFlow(emptyList())
    val routeStopPoints = _routeStopPoints

    private val _eventReport: MutableStateFlow<List<EventReport>> = MutableStateFlow(emptyList())
    val eventReport = _eventReport
    
    private val _eventPoint: MutableStateFlow<List<Position>> = MutableStateFlow(emptyList())
    val eventPoint: StateFlow<List<Position>> = _eventPoint

    val highLightedTrips: MutableStateFlow<List<LatLng>> = MutableStateFlow(emptyList())
    //Chart Data temperature and fuel
    private val _chartData: MutableLiveData<ChartData> = MutableLiveData()
    val chartData = _chartData
    //show Toast
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()
    //Jobs
    private val jobs = ArrayList<Job>()
    //handler
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.d("Cancel", "Cancel Handler: ${throwable.message}")
    }

    val graphBottomSheetState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private var webSocket: WebSocket? = null
    private var doeSohApi: DoeSohApi? = null

    init {
        //Log in from shareReference
        if(usernamePreference?.isNotEmpty() == true && passwordPreference?.isNotEmpty() == true && shortUrlPreference != null) {
            val url = Constant.BASE_URL_MAP.getOrDefault(shortUrlPreference,
                Constant.BASE_URL
            )
            Log.d("Preference", " $usernamePreference : $passwordPreference : $shortUrlPreference: $url")
            logIn(usernamePreference,passwordPreference,url,shortUrlPreference)
        }
        // Notification state change update FCM token in server
        viewModelScope.launch {
            _notificationEnabled.collect {
                user.value?.let { user ->
                    synFcmToken(user)
                    sharedPreferences.edit().putBoolean(GpsEventNotification.NOTIFICATION_ENABLE,it).apply()
                }
            }
        }

    }

    private fun createWebSocketConnection(baseUrl: String) {
        val request = Request.Builder().url(baseUrl + "api/socket").build()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "onOpen: " + "WebSocket connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                var data = Gson().fromJson(text,Update::class.java)
                Log.d(TAG, "onMessage: $text")
                if(data != null) {
                    handleUpdateData(data)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d(TAG, "onFailure: " + t.localizedMessage)
            }
        }
        webSocket  = client.newWebSocket(request,webSocketListener)

    }

    private fun handleUpdateData(update: Update) {
        //Somehow I need to check it's null or not
        //Don't Delete This !!
//        Log.d("CheckThread:", "${Thread.currentThread().name}")
        if(update.events != null && update.events.isNotEmpty()) {
        }
        if(update.devices != null && update.devices.isNotEmpty()) {
            update.devices.forEach { device ->
                //get latest device status
                latestDeviceStatus[device.id] = device.status
                //only come one device each I think
                if(!deviceLists.containsKey(device.id)) {
                    deviceLists[device.id] = device
                    markerStateList[device.id] = MarkerState()
                    _devices.postValue(deviceLists)

                }
            }
        }

        if(update.positions != null && update.positions.isNotEmpty()) {
            update.positions.forEach {  position ->
                val deviceId = position.deviceId
                val newPosition = LatLng(position.latitude,position.longitude)
//                if(markerStateList[deviceId]?.position == newPosition) {
//                    Log.d("MarkerPosition", " is same")
//                }
                latestDevicePositionList[deviceId] = position
                _latestPositions.postValue(latestDevicePositionList)
                markerStateList[deviceId]?.position = newPosition
//
            }
        }
    }

    // LogIn
    private fun logIn(email:String,password:String,baseUrl: String,shortUrl: String) {
        //Build retrofit with given URL
        currentUrl = baseUrl
        doeSohApi = retrofit.newBuilder().baseUrl(baseUrl).build().create(
            DoeSohApi::class.java)
        doeSohApi?.let { api ->
            LogInUseCase(email,password,api).onEach {  result ->
                when(result) {
                    is Resource.Success -> {
                        val user = result.data
                        _user.value = user
                        //FCM Token
                        user?.let {
                            synFcmToken(it)
                        }
                        //Save Preferences
                        saveUserCredentials(username = email,password = password,shortUrl = shortUrl)
                        Log.d(TAG, "${user?.name}")
//                        val cookie = CookieManager().cookieStore.cookies
//                        Log.d(TAG, "Cookies  = $cookie")
//                        withContext(Dispatchers.IO) {
                            try {
                                //Device needed to query first before websocket update
                                val getDevices = api.getDevices()
                                getDevices.forEach { device ->
                                    deviceLists[device.id] = device
                                    latestDeviceStatus[device.id] = device.status
                                    markerStateList[device.id] = MarkerState()

                                }
                                _devices.postValue(deviceLists)
                                createWebSocketConnection(baseUrl)
                                _logInState.value = LogInState(isAuth = true)
                            } catch (e: Exception) {
                                _toastEvent.emit("${e.message}")
                                Log.d(TAG, "${e.localizedMessage} in LogIn")
                            }
//                        }

                    }
                    is Resource.Error -> {
                        _toastEvent.emit(result.message ?: "An unexpected error occurred")
                        _logInState.value = LogInState(error = result.message ?: "An unexpected error occurred")
                    }
                    is Resource.Loading -> {
                        _logInState.value = LogInState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    // Log Out
    private fun logOut() {
        webSocket?.cancel()
        with(sharedPreferences.edit()) {
            remove(USERNAME_PREFERENCE)
            remove(PASSWORD_PREFERENCE)
            remove(SHORT_URL_PREFERENCE)
            commit()
        }
        _logoutFlow.value = true

    }

    // Get Report to Show Route in RouteDetail
     private suspend fun getRoute(date: DateTimeISO,deviceIdList: List<Long>) {
        withContext(Dispatchers.Default) {
            try {
                _reportDetailUIState.value = ReportDetailUIState(isLoading = true, showReports = false)
                val route = async { doeSohApi?.getRoute(deviceIdList, emptyList(),date.startDateTime,date.endDateTime) }
                val trip = async {doeSohApi?.getTrip(deviceIdList, emptyList(),date.startDateTime,date.endDateTime)}
                val stop = async {doeSohApi?.getStop(deviceIdList, emptyList(),date.startDateTime,date.endDateTime)}
//            route.await()?.forEach { position ->
//                Log.d("Report", "reportRoute: ${position.fixTime} ")
//                Log.d("Report", "reportRoute: ${position.address} ")
//            }
                // HoLD UP
//                route.await()?.let { positions ->
//                    routeReportPolylineList = positions.map { position ->
//                        Log.d("Report", "Route: ${position.fixTime}")
//                        Log.d("Report", "Route: ${position.latitude}")
//                        Log.d("Report", "Route: ${position.longitude}")
//                        LatLng(position.latitude,position.longitude)
//
//                    }
//                }
                route.await()?.let { positions ->
                    routeReportPolylineList = positions
                }

                _routeReportPolyLine.value = routeReportPolylineList.map {
                    LatLng(it.latitude,it.longitude)
                }
//            _tripReport.value = trip.await()!!
                trip.await()?.let { tripReports ->
                    _tripReport.value = tripReports
                }
//            stop.await()?.forEach { stopReport ->
//                Log.d("Report", "Stop: ${stopReport.address}")
//            }

                stop.await()?.let { stopReports ->
                    _routeStopPoints.value = stopReports
                }
                markerVisibility.value = false
                showingRoute.value = true
                _reportDetailUIState.value = ReportDetailUIState(showRoutes = true, showReports = false)
            } catch (e: CancellationException) {
                _toastEvent.emit("it did cancel")
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                Log.d("Cancel", "getRoute: Route Job is cancel ${e.message}")
            } catch (e: Exception) {
                _toastEvent.emit("${e.message}")
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                e.localizedMessage?.let { Log.d("ReportRoute", it) }
            }
        }

    }

    //Event Report
    private suspend fun getEvent(date: DateTimeISO,deviceIdList: List<Long>) {
        _reportDetailUIState.value = ReportDetailUIState(isLoading = true, showReports = false)
        withContext(Dispatchers.IO) {
            try {
                val event = doeSohApi?.getEvent(deviceIdList, emptyList(),date.startDateTime,date.endDateTime)
                event?.let { eventReport ->
                    _eventReport.value = eventReport
//                    _eventReport.value.forEach { event ->
//                        Log.d(TAG, "getEvent: ${event.deviceId} : ${event.serverTime}")
//                        Log.d(TAG, "getEvent: ${event.type}")
//                        event.attributes.forEach { (key, value) ->
//                            Log.d(TAG, "getEvent: $key : $value")
//                        }
//                    }
                }
                _reportDetailUIState.value = ReportDetailUIState(showEvents = true, showReports = false)

            }catch (e: Exception) {
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                e.localizedMessage?.let { Log.d("Report", it) }
            }
        }
    }

    private suspend fun getTemperature(date: DateTimeISO,deviceIdList: List<Long>) {
//        Log.d(TAG, "getTemperature: $date : ${deviceIdList[0]}")
        _reportDetailUIState.value = ReportDetailUIState(isLoading = true, showReports = false)
        withContext(Dispatchers.Default) {
            val temperatureIntList = ArrayList<Int>()
            val temperatureList = ArrayList<Temperature>()
            var filteredTemperatureList = ArrayList<Temperature>()
            var temperatureDataset: TemperatureDataset? = null
            try {
                val routePosition = async { doeSohApi?.getRoute(deviceIdList, emptyList(),date.startDateTime,date.endDateTime) }
                routePosition.await()?.let { positions ->
                    //TODO:: Calculate Temperature
                    if(positions.isNotEmpty()) {
                        positions.forEach { position ->
                            if(position.attributes.containsKey("deviceTemp")) {
                                val temp = position.attributes["deviceTemp"] as Double
                                val tempInt = temp.roundToInt()
                                val date = Utilities.getUserDateFormat(true,position.fixTime)
                                val address = position.address ?: "${position.latitude} - ${position.longitude}"
                                temperatureIntList.add(tempInt)
                                temperatureList.add(Temperature(tempInt,date,address))
                            }
                        }

                        if(temperatureList.isNotEmpty() && temperatureIntList.isNotEmpty()) {
                            filteredTemperatureList = TemperatureUtil.filterTemperatureList(temperatureList)
                            temperatureDataset = TemperatureUtil.findLowestHighestAverage(temperatureIntList)
                        }
                    }

                }

                if(filteredTemperatureList.isNotEmpty()) {
                    temperatureDataset?.let { temperatureDataset ->
                        _chartData.postValue(ChartData.ChartTemperatureData(filteredTemperatureList,temperatureDataset,date.queryDateString))
                    }
                }
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                graphBottomSheetState.value = true

            }catch (e: Exception) {
                _toastEvent.emit("${e.message}")
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                Log.d(TAG, "getTemperature: ${e.localizedMessage}")
            }

        }


    }
    //Fuel
    private suspend fun getFuel(date: DateTimeISO,deviceIdList: List<Long>) {
        _reportDetailUIState.value = ReportDetailUIState(isLoading = true, showReports = false)
        withContext(Dispatchers.Default) {
            try {
                var filteredFuelList = ArrayList<Fuel>()
                val routePosition = async { doeSohApi?.getRoute(deviceIdList, emptyList(),date.startDateTime,date.endDateTime) }
                routePosition.await()?.let { positions ->
                  filteredFuelList = FuelUtil.filterFuelData(positions)
                }
                if(filteredFuelList.isNotEmpty()) {
                    _chartData.postValue(ChartData.ChartFuelData(filteredFuelList,"20","300",date.queryDateString))
                }
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                graphBottomSheetState.value = true
            }catch (e: Exception) {
                _toastEvent.emit("${e.localizedMessage}")
                _reportDetailUIState.value = ReportDetailUIState(isLoading = false, showReports = true)
                Log.d(TAG, "getFuel: ${e.localizedMessage}")
            }
        }
    }
    //Engine Cut/Restore Command
    private fun sendCommand(device: Device,type: CommandType) = viewModelScope.launch {
        try {
            val gt06Attributes = device.attributes.containsKey("GT06")
            val engineRestoreCommand = if (gt06Attributes) mapOf("data" to "RELAY,0#") else mapOf("data" to "HFYD,000000#")
            val engineCutCommand = if(gt06Attributes) mapOf("data" to "RELAY,1#") else mapOf("data" to "DYD,000000#")
            var responseText = ""
            val sendCommandData = when(type) {
                CommandType.EngineCut -> {
                    responseText = "Engine Cut"
                    engineCutCommand
                }
                CommandType.EngineRestore -> {
                    responseText = "Engine Restore"
                    engineRestoreCommand
                }
            }
            val command = Command(deviceId = device.id, attributes = sendCommandData)
            val send = doeSohApi?.sendCommand(command)
            if(send?.type == "custom") {
                _toastEvent.emit("$responseText Command Send Successfully!")
            }else {
                _toastEvent.emit("$responseText Command Send Failed")
            }
            Log.d(TAG, "sendCommand: ${send?.type}")
        }catch (e: Exception) {
            _toastEvent.emit("Something went wrong!")
            Log.d(TAG, "sendCommand: ${e.localizedMessage}: Oops")
        }
    }
    // Save FCM Token to User Account
    private fun synFcmToken(user: User) {
         var updatedUserAttribute: User? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

                val token = task.result
                val currentUserToken = user.attributes.getOrDefault("notificationTokens","")
                val newAttributes = user.attributes
                Log.d(TAG, "synFcmToken: $token : $currentUserToken")
                Log.d("Token", token)
                //Token is not null
                token?.let { token ->
                        //notification on
                        if(_notificationEnabled.value) {
                            if(token == currentUserToken) {
                                Log.d("Fcm", "synFcmToken: User setup token already")
                                return@OnCompleteListener
                            }else {
                                newAttributes["notificationTokens"] = token
                                user.attributes = newAttributes
                                updatedUserAttribute = user

                                viewModelScope.launch {
                                    _toastEvent.emit("Notification Enable Successfully!")
                                }
                                Log.d("Fcm", "synFcmToken: ${updatedUserAttribute?.attributes?.get("notificationTokens")}")
                            }
                        //notification off
                        }else {
                            if(user.attributes.containsKey("notificationTokens")) {
                                user.attributes.remove("notificationTokens")
                                updatedUserAttribute = user
                                viewModelScope.launch {
                                    _toastEvent.emit("Notification Disabled")
                                }
                                Log.d("Fcm", "synFcmToken: Notification token Removed")
                            }else {
                                Log.d("Fcm", "synFcmToken: Notification off")
                                return@OnCompleteListener
                            }
                        }
                }

            //update user attributes notification token
            updatedUserAttribute?.let { updatedUserAttribute ->
                Log.d(TAG, "synFcmToken: Start Updating")
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        doeSohApi?.updateUser(id = updatedUserAttribute.id, user = updatedUserAttribute)
                        Log.d(TAG, "synFcmToken: UpdatedComplete")
                    }catch (e: Exception) {
                        Log.d(TAG, "synFcmToken: ${e.localizedMessage}")
                    }
                }
            }
        })

    }

    private fun showEventPoint(positionId: Long) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "showEventPoint: $positionId")
        _reportDetailUIState.value = ReportDetailUIState(isLoading = true, showReports = false)
        if(markerVisibility.value)  markerVisibility.value = false
        try {
            val position = doeSohApi?.getPositions(listOf(positionId))
            position?.let { positions ->
                _eventPoint.value = positions
                Log.d(TAG, "showEventPoint: ${eventPoint.value[0]?.address}")
                Log.d(TAG, "showEventPoint: ${eventPoint.value[0]?.fixTime}")
            }
            _reportDetailUIState.value = ReportDetailUIState(showEvents = true, showReports = false)

        }catch (e: Exception) {
            _reportDetailUIState.value = ReportDetailUIState(showEvents = true, showReports = false)
            _toastEvent.emit("Can't Find Position")
            Log.d(TAG, "showEventPoint: ${e.message}")
        }

    }

    private fun showHighLightedTrip(tripReport: TripReport) {
        val startPoint = LatLng(tripReport.startLat,tripReport.startLon)
        val endPoint = LatLng(tripReport.endLat,tripReport.endLon)
        val startIndex = routeReportPolyLine.value.indexOf(startPoint)
        val endIndex = routeReportPolyLine.value.indexOf(endPoint) + 1
        val highLightedTripList = routeReportPolyLine.value.slice(startIndex..endIndex)
        highLightedTrips.value = highLightedTripList
        Log.d(TAG, "showHighLightedTrip: ${highLightedTrips.value.size}")
    }


    fun onLoginEvent(event: LogInEvent) {
        when(event) {
            is LogInEvent.Authenticate -> {
                    logIn(event.email,event.password,event.url,event.shortUrl)

            }
        }
    }

    // Report Detail Of Requested Type
    private fun reportDetail(deviceId: Long,reportDate: ReportDate,reportType: ReportType) = viewModelScope.launch(handler) {
        val date = when(reportDate) {
            is ReportDate.Today -> Utilities.getTodayDateISO()
            is ReportDate.Yesterday -> Utilities.getYesterdayDateISO()
            is ReportDate.Custom -> Utilities.customDateISO(reportDate.date)
        }
        val deviceIdList = listOf(deviceId)

        when(reportType) {
            is ReportType.Route -> {
                getRoute(date,deviceIdList)
            }
            is ReportType.Event -> {
                getEvent(date,deviceIdList)
            }
            is ReportType.Temperature -> {
                getTemperature(date,deviceIdList)
            }
            is ReportType.Fuel -> {
                getFuel(date,deviceIdList)
            }
        }
    }

    fun onReportDetailEvent(event: ReportDetailEvent) {
        when(event) {
            is ReportDetailEvent.RequestRoute -> {
//                getRoute(event.deviceId,event.reportDate)
            }
            is ReportDetailEvent.RequestEvent -> {

            }
            is ReportDetailEvent.RequestTemperature -> {
            }
            is ReportDetailEvent.RequestFuel -> {

            }
            is ReportDetailEvent.Close -> {
                reportMode.value = null
                showingRoute.value = false
                markerVisibility.value = true
                _eventPoint.value = emptyList()
                routeReportPolylineList = emptyList()
                _reportDetailUIState.value = ReportDetailUIState()
                highLightedTrips.value = emptyList()
                stopMarkerVisiable.value = true
                _chartData.value = null

                //clear job when close button is clicked
                if(jobs.isNotEmpty()) {
                    for (job in jobs) {
                        job.cancel()
                    }
                    jobs.clear()
                }
            }
            is ReportDetailEvent.Back -> {
                showingRoute.value = false
                markerVisibility.value = true
                routeReportPolylineList = emptyList()
                _reportDetailUIState.value = ReportDetailUIState()
                highLightedTrips.value = emptyList()
                stopMarkerVisiable.value = true
            }
            is ReportDetailEvent.Minimize -> {

            }
            is ReportDetailEvent.ReportDetailRequest -> {
                // Report detail
                val reportDetailJob = reportDetail(event.deviceId,event.reportDate,event.reportType)
                jobs.add(reportDetailJob)
            }
            is ReportDetailEvent.ShowEventPoint -> {
                val eventPointJob = showEventPoint(event.positionId)
                jobs.add(eventPointJob)
            }
            is ReportDetailEvent.ShowHighLightedTrip -> {
                showHighLightedTrip(event.tripReport)
            }
            is ReportDetailEvent.SendCommand -> {
                sendCommand(event.device,event.type)
            }
        }
    }

    fun onMapUIEvent(event: MapUIEvent) {
        when(event) {
            is MapUIEvent.ShowToast -> {}
            is MapUIEvent.LogOut -> {
                logOut()
            }
        }
    }

    fun setNotification(notificationState: Boolean) {
        _notificationEnabled.value = notificationState
    }

    private fun saveUserCredentials(username: String, password: String, shortUrl: String) {
        with(sharedPreferences.edit()) {
            putString(USERNAME_PREFERENCE,username)
            putString(PASSWORD_PREFERENCE,password)
            putString(SHORT_URL_PREFERENCE,shortUrl)
            apply()
        }
    }

    sealed class MapUIEvent {
//        data class ReportMode(val deviceId: Long): OnMapUIEvent()
        data class ShowToast(val text: String): MapUIEvent()
        object LogOut: MapUIEvent()
    }


    override fun onCleared() {
        super.onCleared()
        webSocket?.cancel()
        doeSohApi = null
        val cookieManager = CookieManager()
        cookieManager.cookieStore.removeAll()
        Log.d("mainviewmodel", "onCleared: viewmodel is cleared ")
//        if(cookieManager.hasCookies()) {
//            cookieManager.removeAllCookies(ValueCallback {
//
//            })
//        }
    }
}