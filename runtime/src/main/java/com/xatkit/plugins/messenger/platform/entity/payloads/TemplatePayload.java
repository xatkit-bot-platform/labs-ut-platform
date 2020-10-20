package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public abstract class TemplatePayload implements Payload {
    @SerializedName(value = "template_type")
    @Getter
    private final String templateType;

    public TemplatePayload(String templateType) {
        this.templateType = templateType;
    }
}
