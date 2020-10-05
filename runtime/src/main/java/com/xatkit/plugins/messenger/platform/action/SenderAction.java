package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.annotations.SerializedName;

public enum SenderAction {
    @SerializedName("typing_on")
    typingOn,
    @SerializedName("typing_off")
    typingOff,
    @SerializedName("mark_seen")
    markSeen
}
