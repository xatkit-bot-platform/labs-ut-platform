package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.payloads.GeneralPayload;
import lombok.Getter;

public class NonTemplatePayload implements GeneralPayload {
    @Getter
    private final String url;
    @SerializedName(value = "is_reusable")
    @Getter
    private final boolean isReusable;

    public NonTemplatePayload(final String url, final boolean isReusable) {
        this.url = url;
        this.isReusable = isReusable;
    }
}
