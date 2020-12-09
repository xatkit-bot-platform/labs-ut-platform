package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import com.xatkit.plugins.messenger.platform.entity.payloads.Payload;
import com.xatkit.plugins.messenger.platform.entity.payloads.TemplatePayload;
import lombok.Getter;

/**
 * Used for sending attachments such as files or templates.
 * Media/attachment (audio, video, files) other than images
 * won't be supported by Facebook for European users since 16th of December 2020.
 * @see Message
 */
public class Attachment {
    @Getter
    private final AttachmentType type;
    @Getter
    private final Payload payload;

    // TODO: Support files and templates as well
    public enum AttachmentType {
        @Deprecated
        @SerializedName("audio")
        audio,
        @Deprecated
        @SerializedName("video")
        video,
        @SerializedName("image")
        image,
        @SerializedName("template")
        template,
        @Deprecated
        @SerializedName("file")
        file
    }

    public Attachment(final AttachmentType type, final Payload payload) {
        this.type = type;
        this.payload = payload;
    }

    public Attachment(final TemplatePayload payload) {
        this(AttachmentType.template, payload);
    }
}
