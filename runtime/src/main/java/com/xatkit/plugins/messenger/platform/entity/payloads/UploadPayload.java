package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class UploadPayload implements Payload {
    @Getter
    private final String url;
    @SerializedName(value = "is_reusable")
    @Getter
    private final boolean isReusable;

    public UploadPayload(final String url, final boolean isReusable) {
        this.url = url;
        this.isReusable = isReusable;
    }
}
