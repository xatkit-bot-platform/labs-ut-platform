package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class FilePayload implements Payload {
    @SerializedName(value = "is_reusable")
    @Getter
    private final boolean isReusable;

    public FilePayload(final boolean isReusable) {
        this.isReusable = isReusable;
    }
}
