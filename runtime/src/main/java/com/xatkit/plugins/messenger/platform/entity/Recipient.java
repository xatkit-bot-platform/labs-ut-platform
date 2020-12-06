package com.xatkit.plugins.messenger.platform.entity;

import lombok.Getter;

/**
 * Holds the recipient of the message
 * @see Messaging
 */
public class Recipient {
    @Getter
    private final String id;

    public Recipient(String id) {
        this.id = id;
    }
}
