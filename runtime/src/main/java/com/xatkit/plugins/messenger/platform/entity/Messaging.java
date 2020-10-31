package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class Messaging {
    @Getter
    private final Recipient recipient;
    @Getter
    private final Message message;
    @Getter
    @SerializedName(value = "sender_action")
    private final SenderAction senderAction;

    public Messaging(Recipient recipient, SenderAction senderAction) {
        this.recipient = recipient;
        this.senderAction = senderAction;
        this.message = null;
    }

    public Messaging(String recipient, SenderAction senderAction) {
        this(new Recipient(recipient), senderAction);
    }

    public Messaging(Recipient recipient, Message message) {
        this.recipient = recipient;
        this.message = message;
        this.senderAction = null;
    }

    public Messaging(String recipent, Message message) {
        this(new Recipient(recipent), message);
    }
}
