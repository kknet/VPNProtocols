package com.tim.vpnprotocols.compose.openvpn.controller

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tim.basevpn.state.ConnectionState
import com.tim.openvpn.OpenVPNConfig
import com.tim.openvpn.delegate.openVPN
import com.tim.vpnprotocols.compose.base.BaseController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @Author: Timur Hojatov
 */
@Composable
fun rememberOpenVPNController(config: OpenVPNConfig): OpenVPNController {
    val context = LocalContext.current
    val registry = LocalActivityResultRegistryOwner.current
    return remember(context) {
        OpenVPNController(
            activityResultRegistryOwner = registry!!,
            config = config
        )
    }
    // TODO find way to retrieve registry from compose context
}

class OpenVPNController(
    activityResultRegistryOwner: ActivityResultRegistryOwner,
    config: OpenVPNConfig
) : BaseController {

    private val mutableStateFlow = MutableStateFlow(ConnectionState.IDLE)
    override val connectionState: StateFlow<ConnectionState> = mutableStateFlow.asStateFlow()

    private val vpnService by activityResultRegistryOwner.openVPN(
        config = config
    ) { connectionStatus ->
        mutableStateFlow.value = connectionStatus
    }

    override fun startVpn() {
        mutableStateFlow.value = ConnectionState.CONNECTING
        vpnService.start()
    }

    override fun stopVpn() {
        mutableStateFlow.value = ConnectionState.DISCONNECTING
        vpnService.stop()
    }
}
