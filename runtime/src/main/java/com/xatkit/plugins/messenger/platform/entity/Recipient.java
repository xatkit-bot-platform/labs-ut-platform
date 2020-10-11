package com.xatkit.plugins.messenger.platform.entity;

import lombok.Getter;

public class Recipient {
    @Getter
    private final String id;

    public Recipient(String id) {
        this.id = id;
    }
}
