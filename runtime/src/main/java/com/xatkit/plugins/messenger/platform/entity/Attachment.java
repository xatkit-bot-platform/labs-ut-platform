package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.payloads.GeneralPayload;
import com.xatkit.plugins.messenger.platform.entity.payloads.GenericTemplatePayload;
import com.xatkit.plugins.messenger.platform.entity.payloads.NonTemplatePayload;
import lombok.Getter;

public class Attachment {
    @Getter
    private final AttachmentType type;
    @Getter
    private final GeneralPayload payload;

    // TODO: Support files and templates as well
    public enum AttachmentType {
        @SerializedName("audio")
        audio,
        @SerializedName("video")
        video,
        @SerializedName("image")
        image,
        @SerializedName("template")
        template
    }

    public Attachment(final AttachmentType type, final NonTemplatePayload payload) {
        this.type = type;
        this.payload = payload;
    }

    public Attachment(final GenericTemplatePayload genericTemplatePayload) {
        this.type = AttachmentType.template;
        this.payload = genericTemplatePayload;
    }
}
