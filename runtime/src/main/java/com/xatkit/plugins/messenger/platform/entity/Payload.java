package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Payload {
    @Getter
    private final String url;
    @SerializedName(value = "is_reusable")
    @Getter
    private final boolean isReusable;

    public Payload(final String url, final boolean isReusable) {
        this.url = url;
        this.isReusable = isReusable;
    }
}
