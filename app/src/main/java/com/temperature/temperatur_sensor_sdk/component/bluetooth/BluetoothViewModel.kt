package com.temperature.temperatur_sensor_sdk.component.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.temperature.temperatur_sensor_sdk.TemperatureDatabase
import com.temperature.temperatur_sensor_sdk.entity.TemperatureRecord
import com.temperature.temperatur_sensor_sdk.util.BluetoothUtil.getBluetoothState
import com.temperature.temperatur_sensor_sdk.util.BluetoothUtil.hasRequiredPermissions
import com.temperature.temperatur_sensor_sdk.util.BluetoothUtil.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

/**
 * 管理藍芽的狀態，與裝置管理列表
 */
class BluetoothViewModel(
    context: Application
) : AndroidViewModel(context) {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

    private val context = getApplication<Application>()
    private val dao = TemperatureDatabase.dao

    private val _latestData = MutableStateFlow<TemperatureRecord?>(null)

    private val _state = MutableStateFlow(getBluetoothState(bluetoothAdapter, context))

    val state: StateFlow<BluetoothState> = _state.asStateFlow()

    private val _devices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val devices: StateFlow<List<BluetoothDeviceInfo>> = _devices.asStateFlow()

    private val _receivedData = MutableStateFlow<ByteArray?>(null)

    private val dataBuffer = StringBuilder()
    private var isCollectingData = false

    // LiveData for complete JSON data
    private val _completeData = MutableLiveData<String>()

    // 掃描回調函式
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            viewModelScope.launch(Dispatchers.Default) {
                val device = result.device
                // 使用多種方式嘗試獲取設備名稱
                val deviceName = getDeviceName(result, device)

                val deviceInfo = BluetoothDeviceInfo(
                    device = device,
                    name = deviceName,
                    address = device.address,
                    isConnected = false
                )

                withContext(Dispatchers.Main) {
                    if (!_devices.value.any { it.address == deviceInfo.address }) {
                        _devices.value += deviceInfo
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BluetoothDebug", "Scan Failed: $errorCode")
            viewModelScope.launch(Dispatchers.Main) {
                _state.value = BluetoothState.Ready
                showToast("掃描失敗，錯誤碼: $errorCode", context)
            }
        }
    }

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    if (hasRequiredPermissions(context)) {
                        _state.value = BluetoothState.Connected
                        updateDeviceConnectionState(gatt.device, true)
                        gatt.discoverServices()
                    } else {
                        // 沒有權限時，中斷連接
                        _state.value = BluetoothState.Disabled
                        closeConnection()
                    }
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    _state.value = BluetoothState.Disabled
                    updateDeviceConnectionState(gatt.device, false)
                    closeConnection()
                }
            }
        }

        // 當特徵值發生變化時觸發（通知）
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val receivedText = String(value)
            processReceivedData(receivedText)
            _receivedData.value = value
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("onServicesDiscovered", "處理服務完成")
                processServices(gatt.services)
            }
        }
    }

    // 處理數據訂閱
    @SuppressLint("MissingPermission")
    private fun processServices(services: List<BluetoothGattService>) {
        try {
            for (service in services) {
                Log.d("BluetoothViewModel", "處理服務 UUID: ${service.uuid}")

                // 特別處理 FFE0 服務
                if (service.uuid.toString().uppercase().contains("FFE0")) {
                    for (characteristic in service.characteristics) {
                        if (characteristic.uuid.toString().uppercase().contains("FFE1")) {
                            Log.d("BluetoothViewModel", "找到目標特徵值: ${characteristic.uuid}")

                            // 1. 先設置通知
                            bluetoothGatt?.setCharacteristicNotification(characteristic, true)

                            // 2. 獲取並設置描述符
                            characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                                ?.let { descriptor ->
                                    viewModelScope.launch(Dispatchers.IO) {
                                        try {
                                            // 確保先前的操作完成
                                            delay(500)

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                val success = bluetoothGatt?.writeDescriptor(
                                                    descriptor,
                                                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                                )
                                                Log.d(
                                                    "BluetoothViewModel",
                                                    "描述符寫入嘗試 (新API): $success"
                                                )

                                            } else {
                                                // Android 12 及以下版本
                                                @Suppress("DEPRECATION")
                                                descriptor.value =
                                                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                                @Suppress("DEPRECATION")
                                                val success =
                                                    bluetoothGatt?.writeDescriptor(descriptor)
                                                Log.d(
                                                    "BluetoothViewModel",
                                                    "描述符寫入嘗試 (舊API): $success"
                                                )
                                            }
                                        } catch (e: Exception) {
                                            Log.e("BluetoothViewModel", "設置描述符時發生錯誤", e)
                                        }
                                    }
                                } ?: Log.e("BluetoothViewModel", "找不到通知描述符")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BluetoothViewModel", "處理服務時發生錯誤", e)
        }
    }

    // 開始掃描
    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!hasRequiredPermissions(context)) {
            _state.value = BluetoothState.Disabled
            showToast("缺少必要的藍牙權限", context)
            return
        }

        if (bluetoothAdapter?.isEnabled != true) {
            _state.value = BluetoothState.Disabled
            showToast("藍牙未啟用", context)
            return
        }

        _state.value = BluetoothState.Scanning
        _isScanning.value = true

        val scanner = bluetoothAdapter.bluetoothLeScanner
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        viewModelScope.launch {
            try {
                scanner?.startScan(null, settings, scanCallback)
                delay(10000)
                stopScan()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _state.value = BluetoothState.Ready
                    showToast("掃描發生錯誤: ${e.message}", context)
                }
            }
        }
    }

    // 結束掃描
    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        if (_state.value == BluetoothState.Scanning) {
            _state.value = BluetoothState.Ready
        }
        _isScanning.value = false
    }

    // 點擊連結裝置藍芽
    @SuppressLint("MissingPermission")
    fun connectDevice(deviceInfo: BluetoothDeviceInfo) {
        if (!hasRequiredPermissions(context)) {
            return
        }

        bluetoothGatt?.close()
        bluetoothGatt = deviceInfo.device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    private fun closeConnection() {
        if (hasRequiredPermissions(context)) {
            bluetoothGatt?.close()
            bluetoothGatt = null
        }
    }

    private fun updateDeviceConnectionState(device: BluetoothDevice, isConnected: Boolean) {
        _devices.value = _devices.value.map {
            if (it.device == device) it.copy(isConnected = isConnected)
            else it
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceName(result: ScanResult, device: BluetoothDevice): String {
        return result.scanRecord?.deviceName?.takeIf { it.isNotEmpty() }
            ?: device.name?.takeIf { it.isNotEmpty() }
            ?: try {
                (device.javaClass.getMethod("getAlias").invoke(device) as? String)
                    ?.takeIf { it.isNotEmpty() }
            } catch (e: Exception) {
                null
            }
            ?: "未知裝置 (${device.address})"
    }

    // 獲取完整 JSON 字串
    private fun processReceivedData(data: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // 檢查是否是 JSON 開始
                if (data.contains("{")) {
                    dataBuffer.clear()
                    isCollectingData = true
                }

                if (isCollectingData) {
                    dataBuffer.append(data)

                    // 檢查是否接收到完整的 JSON
                    if (data.contains("}")) {
                        isCollectingData = false
                        val completeJson = dataBuffer.toString()

                        // 驗證 JSON 格式
                        try {
                            val jsonObject = JSONObject(completeJson)
                            _completeData.postValue(completeJson)
                            Log.d("BluetoothData", "完整數據: $jsonObject")

                            // 將 JSON 轉換為 TemperatureRecord 並存入資料庫
                            val record = TemperatureRecord(
                                pm10 = jsonObject.optDouble("pm1_0", 0.0),
                                pm25 = jsonObject.optDouble("pm2_5", 0.0),
                                pm100 = jsonObject.optDouble("pm10_0", 0.0),
                                temperature = jsonObject.optDouble("temperature", 0.0),
                                humidity = jsonObject.optDouble("humidity", 0.0),
                                status = jsonObject.optString("status", "Unknown"),
                                createTime = LocalDateTime.now()
                            )

                            // 插入數據到資料庫
                            withContext(Dispatchers.IO) {
                                try {
                                    dao.insertTemperatureRecord(record)
                                    dao.refreshLatestRecord()
                                    // 更新最新資料
                                    _latestData.value = record
                                    Log.d("BluetoothData", "數據已成功存入資料庫")
                                } catch (e: Exception) {
                                    Log.e("BluetoothData", "存入資料庫時發生錯誤", e)
                                }
                            }

                            // 清空緩衝區
                            dataBuffer.clear()
                        } catch (e: JSONException) {
                            Log.e("BluetoothData", "JSON 解析錯誤: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("BluetoothData", "處理數據時發生錯誤", e)
                isCollectingData = false
                dataBuffer.clear()
            }
        }
    }

    fun updateBluetoothState(newState: BluetoothState) {
        viewModelScope.launch {
            _state.value = newState
        }
    }

    override fun onCleared() {
        Log.d("onCleared", "藍芽清除")
    }
}

