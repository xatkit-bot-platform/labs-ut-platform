package com.xatkit.plugins.messenger.platform.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Used for creating a messaging object sent to Facebook.
 * This might contain a message or a sender action.
 * Sender actions "typing_on" and "typing_off" won't be supported by Facebook for European users
 * since 16th of December 2020.
 * @see Message
 * @see SenderAction
 */
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
