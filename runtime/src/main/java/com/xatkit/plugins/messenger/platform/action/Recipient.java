package com.xatkit.plugins.messenger.platform.action;

import lombok.Getter;

public class Recipient {
    @Getter
    private final String id;

    public Recipient(String id) {
        this.id = id;
    }
}
