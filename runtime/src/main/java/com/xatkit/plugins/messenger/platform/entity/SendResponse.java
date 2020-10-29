package com.xatkit.plugins.messenger.platform.entity;

import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;

public class SendResponse extends Response {
    @Getter
    private final String recipientId;
    @Getter
    private final String messageId;

    public SendResponse(@NonNull int status, @NonNull String recipientId, @Nullable String messageId) {
        super(status);
        this.recipientId = recipientId;
        this.messageId = messageId;
    }
}
