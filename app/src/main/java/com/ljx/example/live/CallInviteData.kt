package com.ljx.example.live

import java.io.Serializable

data class CallInviteData(
    val channelId: String? = null,
    val calleeToken: String? = null,
    val fromUid: String? = null,
    val toUid: String? = null,
    val clientInfo: ClientInfo? = ClientInfo(),
    val callType: Int? = 0
) : Serializable

data class ClientInfo(
    val isSupportReqGift: Boolean = true,
    val isSupportReqRecharge: Boolean = false,
) : Serializable