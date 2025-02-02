package com.temperature.temperatur_sensor_sdk.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.temperature.temperatur_sensor_sdk.component.BottomBar
import com.temperature.temperatur_sensor_sdk.component.CommonDialog
import com.temperature.temperatur_sensor_sdk.component.DialogState
import com.temperature.temperatur_sensor_sdk.component.DrawerContent
import com.temperature.temperatur_sensor_sdk.component.TopBar
import com.temperature.temperatur_sensor_sdk.route.Screen
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    var dialogState by remember { mutableStateOf(DialogState()) }

    // 新增一個控制 Dialog 的方法
    fun showDialog(
        title: String,
        content: String,
        confirmText: String? = null,
        cancelText: String? = null,
        onConfirm: () -> Unit = {}
    ) {
        dialogState = DialogState(
            isShow = true,
            title = title,
            content = content,
            confirmText = confirmText ?: "apply",
            cancelText = cancelText ?: "cancel",
            onConfirm = onConfirm
        )
    }

    CommonDialog(
        showDialog = dialogState.isShow,
        onDismiss = { dialogState = dialogState.copy(isShow = false) },
        title = dialogState.title,
        content = dialogState.content,
        confirmText = dialogState.confirmText,
        cancelText = dialogState.cancelText,
        onConfirm = {
            dialogState.onConfirm()
            dialogState = dialogState.copy(isShow = false)
        }
    )

    val navController = rememberNavController()
    // 控制抽屜狀態
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 取得當前路由
    val currentRoute = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)
        .value?.destination?.route ?: Screen.Home.route

    // 側邊導航抽屜
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 導航欄
            DrawerContent(
                onHomeClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(Screen.Home.route)
                    }
                },
                onPieClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(Screen.LinePmHistory.route)
                    }
                },
                onLineClick = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate(Screen.LineTempAndHumHistory.route)
                    }
                },
                onSettingsClick = {
                    scope.launch {
                        drawerState.close()
                        // 處理設定點擊
                        showDialog(title = "test", "test", "確認", "取消")
                    }
                }
            )
        }
    ) {
        Scaffold(
            // 頂部欄
            topBar = {
                TopBar(
                    currentRoute = currentRoute
                )
            },
            // 底部欄
            bottomBar = {
                BottomBar(
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onHomeClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onBluetoothClick = {
                        navController.navigate(Screen.Bluetooth.route) {
                            popUpTo(Screen.Bluetooth.route) { inclusive = true }
                        }
                    }
                )
            }
        ) { paddingValues ->
            // 路由
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                Screen.screens.forEach{ screen ->
                    composable(route = screen.route) {
                        screen.content()
                    }
                }
            }
        }
    }
}