package com.xatkit.plugins.messenger.platform.entity.payloads;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class AttachmentIdPayload implements Payload {

    @SerializedName(value = "attachment_id")
    @Getter
    private final String attachmentId;

    public AttachmentIdPayload(final String attachmentId) {
        this.attachmentId = attachmentId;
    }
}
