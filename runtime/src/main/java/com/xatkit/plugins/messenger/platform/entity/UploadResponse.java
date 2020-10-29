package com.xatkit.plugins.messenger.platform.entity;

import lombok.Getter;
import lombok.NonNull;

public class UploadResponse extends Response {
    @Getter
    private final String attachmentId;

    public UploadResponse(@NonNull int status,@NonNull String attachmentId) {
        super(status);
        this.attachmentId = attachmentId;
    }
}
