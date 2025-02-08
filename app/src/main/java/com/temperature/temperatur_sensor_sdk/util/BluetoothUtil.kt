package com.temperature.temperatur_sensor_sdk.util

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.temperature.temperatur_sensor_sdk.component.bluetooth.BluetoothState

object BluetoothUtil {

    fun showToast(message: String, context: Application) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.apply {
            setGravity(Gravity.CENTER, 0, 0)  // 使用 CENTER 會自動置中
            show()
        }
    }


    fun checkBluetoothPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            context.checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    //  check support bluetooth
    fun getBluetoothState(
        bluetoothAdapter: BluetoothAdapter?,
        context: Application
    ): BluetoothState {
        return when {
            bluetoothAdapter == null ->
                BluetoothState.Unsupported

            bluetoothAdapter.isEnabled && hasRequiredPermissions(context) ->
                BluetoothState.Ready

            else -> BluetoothState.Disabled
        }
    }

    // check bluetooth version and permission
    fun hasRequiredPermissions(context: Application): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            context.hasPermission(Manifest.permission.BLUETOOTH) &&
                    context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // check bluetooth
    private fun Context.hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun setupBluetoothPermissionLauncher(
        activity: ComponentActivity,
        onPermissionResult: (Boolean) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            if (!allGranted) {
                showPermissionRationaleDialog(activity)
            }
            onPermissionResult(allGranted)
        }
    }

    private fun showPermissionRationaleDialog(context: ComponentActivity) {
        AlertDialog.Builder(context)
            .setTitle("需要藍牙權限")
            .setMessage("此應用程式需要藍牙權限才能掃描和連接設備。請在設定中開啟權限。")
            .setPositiveButton("前往設定") { _, _ ->
                openAppSettings(context)
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings(context: ComponentActivity) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(this)
        }
    }

}