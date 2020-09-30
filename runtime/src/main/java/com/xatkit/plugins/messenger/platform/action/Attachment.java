package com.xatkit.plugins.messenger.platform.action;

import com.google.gson.annotations.SerializedName;
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
        image
    }

    public Attachment(final AttachmentType type, final Payload payload) {
        this.type = type;
        this.payload = payload;
    }
}
