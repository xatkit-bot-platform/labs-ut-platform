package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.payloads.Payload;
import com.xatkit.plugins.messenger.platform.entity.payloads.TemplatePayload;
import lombok.Getter;

public class Attachment {
    @Getter
    private final AttachmentType type;
    @Getter
    private final Payload payload;

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

    public Attachment(final AttachmentType type, final Payload payload) {
        this.type = type;
        this.payload = payload;
    }

    public Attachment(final TemplatePayload payload) {
        this(AttachmentType.template, payload);
    }
}
