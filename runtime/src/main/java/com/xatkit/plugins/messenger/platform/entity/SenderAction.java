package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Actions that can be sent to Facebook.
 * Sender actions "typing_on" and "typing_off" won't be supported by Facebook for European users
 * since 16th of December 2020.
 *
 * @see Messaging
 */
public enum SenderAction {
    @Deprecated
    @SerializedName("typing_on")
    typingOn,
    @Deprecated
    @SerializedName("typing_off")
    typingOff,
    @SerializedName("mark_seen")
    markSeen
}
