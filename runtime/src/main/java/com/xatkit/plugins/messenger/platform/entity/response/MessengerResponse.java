package com.xatkit.plugins.messenger.platform.entity.response;

import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;

public class MessengerResponse {
    @Getter
    private final String recipientId;
    @Getter
    private final String messageId;
    @Getter
    private final String attachmentId;
    @Getter
    private final int status;

    public MessengerResponse(@NonNull int status, @Nullable String recipientId, @Nullable String messageId, @Nullable String attachmentId) {
        this.status = status;
        this.recipientId = recipientId;
        this.messageId = messageId;
        this.attachmentId = attachmentId;
    }
}
